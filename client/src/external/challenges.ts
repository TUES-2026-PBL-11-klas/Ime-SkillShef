import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  ChallengeDetailSchema,
  ChallengeListSchema,
  type Challenge,
  type ChallengeDetail,
} from "@/schemas/challenges";

/**
 * Backend integration for the Challenges API (`/api/challenges`). Each function
 * maps 1:1 to a backend endpoint.
 *
 * Note: creating a submission is a multipart upload with progress, so it does
 * not go through here — see the route handler at
 * `app/api/challenges/[id]/submissions` and `use-submission-upload`.
 */

export async function fetchChallenges() {
  return http<Challenge[]>({
    method: "GET",
    path: "/api/challenges",
    options: { headers: await getAuthHeaders() },
    schema: ChallengeListSchema,
  });
}

export async function fetchChallenge(challengeId: string) {
  return http<ChallengeDetail>({
    method: "GET",
    path: `/api/challenges/${challengeId}`,
    options: { headers: await getAuthHeaders() },
    schema: ChallengeDetailSchema,
  });
}
