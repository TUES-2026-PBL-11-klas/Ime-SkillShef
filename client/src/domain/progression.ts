/**
 * Pure domain helpers for XP/level presentation (issue #22). Deterministic and
 * framework-free; the authoritative XP curve lives on the backend.
 */

/** Formats an XP amount with thousands separators, e.g. 1234 -> "1,234". */
export function formatXp(xp: number): string {
  return xp.toLocaleString("en-US");
}

/** A signed XP delta for transaction history, e.g. 25 -> "+25", -10 -> "-10". */
export function formatXpDelta(amount: number): string {
  return amount >= 0 ? `+${formatXp(amount)}` : `-${formatXp(Math.abs(amount))}`;
}

/** Clamps a percentage into the 0–100 range (defensive against bad inputs). */
export function clampPercent(percent: number): number {
  if (Number.isNaN(percent)) {
    return 0;
  }
  return Math.min(100, Math.max(0, Math.round(percent)));
}

/** A short human label for an XP transaction reason, e.g. "quiz_passed" -> "Quiz passed". */
export function humanizeReason(reason: string): string {
  const cleaned = reason.replace(/[_-]+/g, " ").trim();
  if (cleaned.length === 0) {
    return "XP awarded";
  }
  return cleaned.charAt(0).toUpperCase() + cleaned.slice(1);
}

/** Ordinal suffix for a leaderboard rank, e.g. 1 -> "1st", 22 -> "22nd". */
export function ordinalRank(rank: number): string {
  const mod100 = rank % 100;
  if (mod100 >= 11 && mod100 <= 13) {
    return `${rank}th`;
  }
  switch (rank % 10) {
    case 1:
      return `${rank}st`;
    case 2:
      return `${rank}nd`;
    case 3:
      return `${rank}rd`;
    default:
      return `${rank}th`;
  }
}
