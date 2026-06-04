import {
  getLeaderboardAction,
  getProgressionAction,
  getRankAction,
  getXpHistoryAction,
} from "@/actions/progression";

/**
 * Client-side wrappers over the progression Server Actions (issue #22).
 */

export function getProgression() {
  return getProgressionAction();
}

export function getRank() {
  return getRankAction();
}

export function getXpHistory(page = 0, size = 20) {
  return getXpHistoryAction(page, size);
}

export function getLeaderboard(limit = 20) {
  return getLeaderboardAction(limit);
}
