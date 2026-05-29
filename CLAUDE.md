# CLAUDE.md

## Documentation Requirement

Before making any code changes, you MUST read the `/Documentation` folder and all of its contents in full. All code changes must be accurate to and consistent with the specifications, requirements, and designs described in the `/Documentation` folder.

This applies to every task, no exceptions — even small edits must be checked against the documentation to ensure correctness.

---

## Client App: Layered Clean Architecture

When working in the `client/` app, you MUST strictly follow the existing layered clean architecture. Before making any changes, read the README file in the relevant layer's directory — it is the authoritative description of that layer's purpose, responsibilities, and rules.

The README files that must be referenced are:

- [client/src/actions/README.md](client/src/actions/README.md)
- [client/src/client/README.md](client/src/client/README.md)
- [client/src/client/actions/README.md](client/src/client/actions/README.md)
- [client/src/client/state/README.md](client/src/client/state/README.md)
- [client/src/components/README.md](client/src/components/README.md)
- [client/src/domain/README.md](client/src/domain/README.md)
- [client/src/external/README.md](client/src/external/README.md)
- [client/src/lib/README.md](client/src/lib/README.md)
- [client/src/schemas/README.md](client/src/schemas/README.md)
- [client/src/services/README.md](client/src/services/README.md)

### Layer Descriptions

#### `src/actions` — Server Actions
Server Actions are the **only server-entry layer** for operations initiated from the UI.
- Accept input from `src/client/actions`.
- Validate input with Zod (using schemas from `src/schemas`).
- Call the Services layer (`src/services`) to execute the use-case.
- Return a serializable result (data + known error shape).
- **No business logic** (belongs in `src/services`). **No direct HTTP calls** (belongs in `src/external` via services).

#### `src/client` — Client-Side Orchestration
Contains client-side orchestration for UI: state hooks and client functions that call Server Actions.
- Sub-layer `src/client/state`: hooks that UI components consume.
- Sub-layer `src/client/actions`: client functions that call `src/actions` (Server Actions).
- Components must not contain logic beyond rendering; they must use hooks from `src/client/state`.
- Client-side actions must be thin wrappers over Server Actions.

#### `src/client/actions` — Client Actions
Client-side functions that take input and call Server Actions in `src/actions`.
- Provide a stable API for hooks (e.g., `createBook(input)`).
- Forward typed input to Server Actions.
- **No validation** (validate in Server Actions with `src/schemas`). **No business logic** (belongs in `src/services`).

#### `src/client/state` — State Hooks
Hooks for UI components; where UI-level orchestration lives.
- Expose ready-to-render state: `data`, `status`, `error`.
- Expose UI handlers: `onSubmit`, `onClick`, etc.
- Call `src/client/actions` for server operations.
- Handle UI concerns: loading flags, optimistic updates, toasts, navigation triggers.
- **No business logic** (belongs in `src/services`).

#### `src/components` — UI Components
UI-only building blocks.
- Render UI given props/state provided by hooks.
- Delegate all data fetching/mutations and business logic to `src/client/state`.
- **No business logic in components.** Keep components focused on composition, accessibility, and presentation.

#### `src/domain` — Domain Logic
Pure domain logic: rules, constants, and functions that model the problem space without frameworks.
- Domain constants/enums.
- Pure helpers that encode business meaning.
- Invariants shared across services and validation.
- **No side effects** (no HTTP, no storage, no React). Keep domain logic reusable and deterministic.

#### `src/external` — Backend Integration
The backend integration layer. Isolates transport details (HTTP, headers, error normalization) from services.
- Provides a shared `http()` abstraction for API calls.
- Organizes API functions in directories that match the Express app structure (e.g., `books/`, `loans/`, `users/`).
- Exposes small functions that map 1:1 to backend endpoints (request in, DTO out).
- **No business rules** (that's `src/services` and `src/domain`). **No React/UI code.**

#### `src/lib` — Generic Utilities
Generic utilities and helpers used across the app.
- Shared utilities (e.g., className merging, formatting, small generic helpers).
- Helpers that are not domain-specific and not transport-specific.
- **No business rules** (use `src/domain` for that).

#### `src/schemas` — Validation Schemas
The **single source of truth** for validation and types.
- Zod schemas for action inputs, forms, and DTO validation.
- TypeScript types inferred from schemas (`z.infer<typeof Schema>`).
- Server Actions must validate inputs using these schemas.
- Prefer sharing schema/types across layers to avoid mismatch between UI, actions, and external DTOs.

#### `src/services` — Application Logic
Implements **all application logic** (use-cases). The core of the app's behavior.
- Implement workflows (e.g., fetch, create, update).
- Orchestrate multiple operations as needed.
- Apply domain rules and transformations using `src/domain`.
- Call `src/external` to communicate with the backend.
- **No UI concerns** (no React, no component state). **No request parsing/validation** (that's `src/actions`).
