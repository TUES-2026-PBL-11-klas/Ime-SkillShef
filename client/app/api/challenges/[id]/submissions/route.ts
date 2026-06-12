import { cookies } from "next/headers";

/**
 * Same-origin proxy for submission uploads.
 *
 * The browser uploads here via XHR so it can track upload *progress* (a Server
 * Action can't stream progress, and the JWT lives in an httpOnly cookie the
 * client JS can't read). This handler runs server-side, reads the auth cookie,
 * and forwards the multipart body to the backend submission endpoint.
 */
export async function POST(
  request: Request,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const baseUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

  const store = await cookies();
  const token = store.get("accessToken")?.value ?? store.get("access_token")?.value;

  const headers: Record<string, string> = {};
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const form = await request.formData();

  const res = await fetch(`${baseUrl}/api/challenges/${id}/submissions`, {
    method: "POST",
    headers,
    body: form,
  });

  const text = await res.text();
  return new Response(text, {
    status: res.status,
    headers: { "Content-Type": res.headers.get("Content-Type") ?? "application/json" },
  });
}
