interface Stat {
  label: string;
  value: number | string;
}

/** Horizontal row of profile stats (XP, level, followers, following). */
export function ProfileStats({ stats }: { stats: Stat[] }) {
  return (
    <div className="flex gap-6">
      {stats.map((s) => (
        <div key={s.label} className="text-center">
          <div className="text-lg font-semibold text-foreground">{s.value}</div>
          <div className="text-xs text-muted-foreground">{s.label}</div>
        </div>
      ))}
    </div>
  );
}
