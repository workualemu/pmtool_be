create table users (
    id uuid primary key,
    email varchar(320) not null,
    password_hash varchar(255) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    enabled boolean not null default true,
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create unique index ux_users_normalized_email on users (lower(email));

create table platform_user_roles (
    user_id uuid not null references users(id) on delete cascade,
    role varchar(64) not null,
    primary key (user_id, role)
);

create table clients (
    id uuid primary key,
    name varchar(255) not null,
    slug varchar(100) not null,
    status varchar(32) not null default 'ACTIVE',
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uq_clients_slug unique (slug),
    constraint ck_clients_status check (status in ('ACTIVE', 'SUSPENDED'))
);

create table client_memberships (
    id uuid primary key,
    client_id uuid not null references clients(id),
    user_id uuid not null references users(id),
    role varchar(32) not null,
    status varchar(32) not null default 'ACTIVE',
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uq_client_memberships_client_user unique (client_id, user_id),
    constraint ck_client_membership_role
      check (role in ('CLIENT_ADMIN', 'MEMBER', 'GUEST')),
    constraint ck_client_membership_status
      check (status in ('ACTIVE', 'SUSPENDED'))
);

create index ix_client_memberships_client_id on client_memberships (client_id);
create index ix_client_memberships_user_id on client_memberships (user_id);

create table client_invitations (
    id uuid primary key,
    client_id uuid not null references clients(id),
    email varchar(320) not null,
    role varchar(32) not null,
    token_hash varchar(128) not null,
    expires_at timestamptz not null,
    status varchar(32) not null default 'PENDING',
    invited_by_user_id uuid references users(id),
    accepted_by_user_id uuid references users(id),
    accepted_at timestamptz,
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    constraint uq_client_invitations_token_hash unique (token_hash),
    constraint ck_client_invitations_role
      check (role in ('CLIENT_ADMIN', 'MEMBER', 'GUEST')),
    constraint ck_client_invitations_status
      check (status in ('PENDING', 'ACCEPTED', 'REVOKED', 'EXPIRED')),
    constraint ck_client_invitations_expiry check (expires_at > created_at)
);

create index ix_client_invitations_client_id on client_invitations (client_id);
create index ix_client_invitations_expires_at on client_invitations (expires_at);

create table refresh_tokens (
    id uuid primary key,
    user_id uuid not null references users(id),
    token_hash varchar(128) not null,
    expires_at timestamptz not null,
    revoked_at timestamptz,
    version bigint not null default 0,
    created_at timestamptz not null default current_timestamp,
    constraint uq_refresh_tokens_token_hash unique (token_hash),
    constraint ck_refresh_tokens_expiry check (expires_at > created_at)
);

create index ix_refresh_tokens_expires_at on refresh_tokens (expires_at);

create table audit_events (
    id uuid primary key,
    client_id uuid references clients(id),
    actor_id uuid references users(id),
    action varchar(128) not null,
    artifact_type varchar(128) not null,
    artifact_id uuid,
    elevated boolean not null default false,
    changes jsonb not null default '{}'::jsonb,
    occurred_at timestamptz not null default current_timestamp
);

create index ix_audit_events_client_id on audit_events (client_id);
create index ix_audit_events_occurred_at on audit_events (occurred_at);
