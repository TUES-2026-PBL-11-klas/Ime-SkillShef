import { ErrorResponseSchema, type ApiResponse } from '@/schemas/api';
import { cookies } from 'next/headers';
import z from 'zod';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

type HttpOptions = {
  body?: unknown;
  headers?: Record<string, string>;
};

type HttpFunctionConfig<T> = {
    method: HttpMethod,
    path: string,
    options?: HttpOptions,
    schema?: z.ZodType<T>
}

/**
 * HTTP abstraction for API calls.
 * Handles authentication, error parsing, and consistent response shape.
 * Must only be called server-side (Server Components / Server Actions).
 */
export async function http<T>({
  method,
  path,
  options,
  schema
}: HttpFunctionConfig<T>): Promise<ApiResponse<T>> {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const url = `${baseUrl}${path}`;

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options?.headers,
  };

  // Automatically attach the JWT access token from cookies when present.
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('accessToken')?.value;
    if (token) headers['Authorization'] = `Bearer ${token}`;
  } catch {
    // cookies() throws outside a request context — safe to ignore.
  }

  const config: RequestInit = { method, headers };

  if (options?.body && method !== 'GET') {
    config.body = JSON.stringify(options.body);
  }

  try {
    const response = await fetch(url, config);
    const status = response.status;

    let json: unknown;
    try {
      json = await response.json();
    } catch {
      return { success: false, error: 'Invalid response format' };
    }

    if (status >= 200 && status < 300) {
      if (schema) {
        const parsed = schema.safeParse(json);
        if (!parsed.success) return { success: false, error: 'Invalid response format' };
      }
      return { success: true, data: json as T };
    }

    const parsedError = ErrorResponseSchema.safeParse(json);
    if (parsedError.success) return { success: false, error: parsedError.data.error };
    return { success: false, error: 'An unexpected error occurred' };
  } catch (error) {
    return { success: false, error: error instanceof Error ? error.message : 'Network error' };
  }
}
