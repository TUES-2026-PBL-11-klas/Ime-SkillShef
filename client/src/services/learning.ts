import {
  fetchLesson,
  fetchLessonPlayback,
  fetchLessonsByNode,
  markLessonWatched,
} from "@/external/lessons";
import { fetchQuiz, fetchQuizzesByNode, submitQuiz } from "@/external/quizzes";
import type { ApiResponse } from "@/schemas/api";
import type {
  LessonPlaybackResponse,
  LessonResponse,
  QuizPublic,
  QuizResult,
  WatchResponse,
} from "@/schemas/skill-tree";

/**
 * Lesson & quiz use-cases (issue #22): playback, watched tracking, quiz
 * delivery and submission. Thin orchestration over the external layer.
 */

export async function getLessonsForNode(nodeId: string): Promise<ApiResponse<LessonResponse[]>> {
  return fetchLessonsByNode(nodeId);
}

export async function getLesson(lessonId: string): Promise<ApiResponse<LessonResponse>> {
  return fetchLesson(lessonId);
}

export async function getLessonPlayback(
  lessonId: string,
): Promise<ApiResponse<LessonPlaybackResponse>> {
  return fetchLessonPlayback(lessonId);
}

export async function watchLesson(lessonId: string): Promise<ApiResponse<WatchResponse>> {
  return markLessonWatched(lessonId);
}

export async function getQuizzesForNode(nodeId: string): Promise<ApiResponse<QuizPublic[]>> {
  return fetchQuizzesByNode(nodeId);
}

export async function getQuiz(quizId: string): Promise<ApiResponse<QuizPublic>> {
  return fetchQuiz(quizId);
}

export async function answerQuiz(
  quizId: string,
  selectedAnswer: string,
): Promise<ApiResponse<QuizResult>> {
  return submitQuiz(quizId, selectedAnswer);
}
