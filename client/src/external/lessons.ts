import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  LessonPlaybackResponseSchema,
  LessonResponseSchema,
  WatchResponseSchema,
  type LessonPlaybackResponse,
  type LessonResponse,
  type WatchResponse,
} from "@/schemas/skill-tree";
import z from "zod";

/**
 * Backend integration for the Lesson API (`/api/lessons`). Each function maps
 * 1:1 to a backend endpoint.
 */

export async function fetchLessonsByNode(nodeId: string) {
  return http<LessonResponse[]>({
    method: "GET",
    path: `/lessons/node/${nodeId}`,
    options: { headers: await getAuthHeaders() },
    schema: z.array(LessonResponseSchema),
  });
}

export async function fetchLesson(lessonId: string) {
  return http<LessonResponse>({
    method: "GET",
    path: `/lessons/${lessonId}`,
    options: { headers: await getAuthHeaders() },
    schema: LessonResponseSchema,
  });
}

export async function fetchLessonPlayback(lessonId: string) {
  return http<LessonPlaybackResponse>({
    method: "GET",
    path: `/lessons/${lessonId}/playback`,
    options: { headers: await getAuthHeaders() },
    schema: LessonPlaybackResponseSchema,
  });
}

export async function markLessonWatched(lessonId: string) {
  return http<WatchResponse>({
    method: "POST",
    path: `/lessons/${lessonId}/watch`,
    options: { headers: await getAuthHeaders() },
    schema: WatchResponseSchema,
  });
}
