/**
 * Pure domain helpers for challenges. No side effects.
 */

export function challengeTypeLabel(type: string): string {
  switch (type) {
    case "skill":
      return "Skill";
    case "recipe":
      return "Recipe";
    case "creative":
      return "Creative";
    default:
      return type;
  }
}

/** Human-friendly "Jun 1 – Jun 7" window for a challenge. */
export function formatChallengeWindow(startDate: string, endDate: string): string {
  const fmt = (iso: string) =>
    new Date(iso).toLocaleDateString(undefined, { month: "short", day: "numeric" });
  return `${fmt(startDate)} – ${fmt(endDate)}`;
}
