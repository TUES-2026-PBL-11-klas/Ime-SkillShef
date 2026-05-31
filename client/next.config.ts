import type { NextConfig } from "next";
import { withSentryConfig } from "@sentry/nextjs";

const nextConfig: NextConfig = {
  /* config options here */
};

export default withSentryConfig(nextConfig, {
  org: process.env.SENTRY_ORG,
  project: process.env.SENTRY_PROJECT,
  authToken: process.env.SENTRY_AUTH_TOKEN,
  // Upload source maps only in CI / Render builds
  silent: !process.env.CI,
  // Automatically tree-shake Sentry logger statements in production
  disableLogger: true,
  // Tunnel Sentry events through Next.js to avoid ad-blocker interference
  tunnelRoute: "/monitoring",
});
