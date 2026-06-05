import { SkillNodeCard } from "@/components/skill-tree/skill-node-card";
import { groupByTier } from "@/domain/skill-tree";
import type { SkillDomain, SkillNode } from "@/schemas/skill-tree";

/**
 * Renders one skill domain (issue #22) with its nodes grouped by tier.
 */
export function DomainSection({
  domain,
  pendingNodeId,
  onOpen,
  onUnlock,
}: {
  domain: SkillDomain;
  pendingNodeId: string | null;
  onOpen: (node: SkillNode) => void;
  onUnlock: (node: SkillNode) => void;
}) {
  const tiers = groupByTier(domain.nodes);

  return (
    <section className="space-y-4">
      <div>
        <h2 className="text-lg font-semibold">{domain.name}</h2>
        {domain.description ? (
          <p className="text-sm text-muted-foreground">{domain.description}</p>
        ) : null}
      </div>

      {tiers.map(({ tier, nodes }) => (
        <div key={tier} className="space-y-2">
          <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
            Tier {tier}
          </p>
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {nodes.map((node) => (
              <SkillNodeCard
                key={node.id}
                node={node}
                pending={pendingNodeId === node.id}
                onOpen={() => onOpen(node)}
                onUnlock={() => onUnlock(node)}
              />
            ))}
          </div>
        </div>
      ))}
    </section>
  );
}
