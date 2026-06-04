"use server";

import z from "zod";
import type { ApiResponse } from "@/schemas/api";
import { QuizSubmissionSchema } from "@/schemas/skill-tree";
import type {
  LessonPlaybackResponse,
  LessonResponse,
  QuizPublic,
  QuizResult,
  WatchResponse,
} from "@/schemas/skill-tree";
import {
  answerQuiz,
  getLessonPlayback,
  getLessonsForNode,
  getQuiz,
  getQuizzesForNode,
  watchLesson,
} from "@/services/learning";

/**
 * Server Actions for lessons and quizzes (issue #22). Validate input with Zod,
 * then delegate to the services layer.
 */

const idSchema = z.uuid();

export async function getLessonsForNodeAction(
  nodeId: string,
): Promise<ApiResponse<LessonResponse[]>> {
  const parsed = idSchema.safeParse(nodeId);
  if (!parsed.success) {
    return { success: false, error: "Invalid node id" };
  }
  return getLessonsForNode(parsed.data);
}

export async function getLessonPlaybackAction(
  lessonId: string,
): Promise<ApiResponse<LessonPlaybackResponse>> {
  const parsed = idSchema.safeParse(lessonId);
  if (!parsed.success) {
    return { success: false, error: "Invalid lesson id" };
  }
  return getLessonPlayback(parsed.data);
}

export async function watchLessonAction(lessonId: string): Promise<ApiResponse<WatchResponse>> {
  const parsed = idSchema.safeParse(lessonId);
  if (!parsed.success) {
    return { success: false, error: "Invalid lesson id" };
  }
  return watchLesson(parsed.data);
}

export async function getQuizzesForNodeAction(
  nodeId: string,
): Promise<ApiResponse<QuizPublic[]>> {
  const parsed = idSchema.safeParse(nodeId);
  if (!parsed.success) {
    return { success: false, error: "Invalid node id" };
  }
  return getQuizzesForNode(parsed.data);
}

export async function getQuizAction(quizId: string): Promise<ApiResponse<QuizPublic>> {
  const parsed = idSchema.safeParse(quizId);
  if (!parsed.success) {
    return { success: false, error: "Invalid quiz id" };
  }
  return getQuiz(parsed.data);
}

export async function submitQuizAction(
  quizId: string,
  selectedAnswer: string,
): Promise<ApiResponse<QuizResult>> {
  const parsedId = idSchema.safeParse(quizId);
  if (!parsedId.success) {
    return { success: false, error: "Invalid quiz id" };
  }
  const parsedBody = QuizSubmissionSchema.safeParse({ selectedAnswer });
  if (!parsedBody.success) {
    return { success: false, error: "Please select an answer" };
  }
  return answerQuiz(parsedId.data, parsedBody.data.selectedAnswer);
}
