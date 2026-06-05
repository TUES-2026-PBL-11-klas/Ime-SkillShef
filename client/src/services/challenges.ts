import * as challengesApi from "@/external/challenges";
import type { ApiResponse } from "@/schemas/api";
import type { Challenge, ChallengeDetail } from "@/schemas/challenges";

/**
 * Challenge use-cases. Thin orchestration over the external API; challenge and
 * submission business logic lives in the backend service.
 */

export async function listChallenges(): Promise<ApiResponse<Challenge[]>> {
  return challengesApi.fetchChallenges();
}

export async function getChallenge(
  challengeId: string,
): Promise<ApiResponse<ChallengeDetail>> {
  return challengesApi.fetchChallenge(challengeId);
}
