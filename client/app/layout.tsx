import type { Metadata } from "next";
import "../src/index.css";
import { PostHogProvider } from "@/components/providers/posthog-provider";
import { SiteHeader } from "@/components/navigation/SiteHeader";
import { SiteFooter } from "@/components/navigation/SiteFooter";

export const metadata: Metadata = {
  title: "SkillChef",
  description: "Learn to cook with a structured skill tree, AI assistance, and community.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <PostHogProvider>
          <div className="flex min-h-screen flex-col">
            <SiteHeader />
            <div className="flex-1">{children}</div>
            <SiteFooter />
          </div>
        </PostHogProvider>
      </body>
    </html>
  );
}
