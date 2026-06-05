import {
  getLessonPlaybackAction,
  getQuizAction,
  submitQuizAction,
  watchLessonAction,
} from "@/actions/learning";

/**
 * Client-side wrappers over the lesson & quiz Server Actions (issue #22).
 */

export function getLessonPlayback(lessonId: string) {
  return getLessonPlaybackAction(lessonId);
}

export function watchLesson(lessonId: string) {
  return watchLessonAction(lessonId);
}

export function getQuiz(quizId: string) {
  return getQuizAction(quizId);
}

export function submitQuiz(quizId: string, selectedAnswer: string) {
  return submitQuizAction(quizId, selectedAnswer);
}
