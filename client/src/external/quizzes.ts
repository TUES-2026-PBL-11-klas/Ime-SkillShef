import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  QuizPublicSchema,
  QuizResultSchema,
  type QuizPublic,
  type QuizResult,
} from "@/schemas/skill-tree";
import z from "zod";

/**
 * Backend integration for the Quiz API (`/api/quizzes`). Each function maps
 * 1:1 to a backend endpoint. Delivery endpoints never include the correct
 * answer (enforced server-side).
 */

export async function fetchQuizzesByNode(nodeId: string) {
  return http<QuizPublic[]>({
    method: "GET",
    path: `/api/quizzes/node/${nodeId}`,
    options: { headers: await getAuthHeaders() },
    schema: z.array(QuizPublicSchema),
  });
}

export async function fetchQuiz(quizId: string) {
  return http<QuizPublic>({
    method: "GET",
    path: `/api/quizzes/${quizId}`,
    options: { headers: await getAuthHeaders() },
    schema: QuizPublicSchema,
  });
}

export async function submitQuiz(quizId: string, selectedAnswer: string) {
  return http<QuizResult>({
    method: "POST",
    path: `/api/quizzes/${quizId}/submit`,
    options: { headers: await getAuthHeaders(), body: { selectedAnswer } },
    schema: QuizResultSchema,
  });
}
