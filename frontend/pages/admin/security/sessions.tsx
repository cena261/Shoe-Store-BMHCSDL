import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import {
  SecurityMetric,
  CodeSnippet,
  ExplainerPanel,
  AuditTable,
  AuditTableColumn,
  FilterBar,
} from '@/modules/admin/components/security/shared';
import { securityAdminService } from '@/common/api/services/securityAdminService';
import { useAdminOnly } from '@/common/hooks/auth';
import type { SecurityContextResponse, SessionAuditResponse } from '@/common/types/backend';

export default function SessionsPage() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [securityContext, setSecurityContext] = useState<SecurityContextResponse | null>(null);
  const [sessionAuditLogs, setSessionAuditLogs] = useState<SessionAuditResponse[]>([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadSessionData();
  }, []);

  const loadSessionData = async () => {
    try {
      setIsLoading(true);
      const [context, sessions] = await Promise.all([
        securityAdminService.getCurrentSession(),
        securityAdminService.getSessionAuditLogs(100),
      ]);

      setSecurityContext(context);
      setSessionAuditLogs(sessions);
    } catch (error) {
      console.error('Failed to load session data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const filteredSessions = sessionAuditLogs.filter(
    (session) =>
      session.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
      session.ipAddress.includes(searchTerm) ||
      session.dbUser.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const sessionColumns: AuditTableColumn<SessionAuditResponse>[] = [
    {
      key: 'sessionId',
      label: 'Session ID',
      width: '120px',
    },
    {
      key: 'username',
      label: 'Username',
      width: '200px',
    },
    {
      key: 'loginAt',
      label: 'Login Time',
      render: (value) => new Date(value).toLocaleString(),
      width: '200px',
    },
    {
      key: 'ipAddress',
      label: 'IP Address',
      width: '150px',
    },
    {
      key: 'dbUser',
      label: 'DB User',
      width: '150px',
    },
    {
      key: 'clientIdentifier',
      label: 'Client ID',
      width: '200px',
    },
  ];

  return (
    <section className="px-4 py-20 sm:px-8 bg-zinc-50 min-h-screen">
      <div className="mx-auto flex max-w-7xl flex-col gap-10">
        <div>
          <button
            onClick={() => router.push('/admin/security/overview')}
            className="text-zinc-500 hover:text-zinc-900 mb-4 flex items-center gap-2 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Back to Security Dashboard
          </button>
          <h1 className="text-4xl font-bold text-zinc-900 mb-2">Session Management</h1>
          <p className="text-zinc-500">
            Requirements 1 & 5: Authentication, Tablespace, Profile, and Session Management
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <SecurityMetric
            label="Current User"
            value={securityContext?.applicationUsername || 'Unknown'}
            subtext={`User ID: ${securityContext?.userId || 'N/A'}`}
            color="blue"
          />
          <SecurityMetric
            label="Total Sessions"
            value={sessionAuditLogs.length}
            subtext="Last 100 logins"
            color="green"
          />
          <SecurityMetric
            label="Tablespace"
            value={securityContext?.defaultTablespace || 'Unknown'}
            subtext="Default tablespace"
            color="yellow"
          />
          <SecurityMetric
            label="Profile"
            value={securityContext?.profileName || 'Unknown'}
            subtext="User profile"
            color="zinc"
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 1: Authentication</h2>

          <ExplainerPanel
            title="How Authentication Works"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'JWT (JSON Web Tokens) based stateless authentication with BCrypt password hashing. When users log in, credentials are verified and a signed JWT is issued containing user claims (userId, email, roles).',
              },
              {
                heading: 'Security Features',
                content:
                  'BCrypt with 10 rounds for password hashing, JWT tokens with expiration, HTTP-only cookies option, role-based access control, and session audit logging for all login attempts.',
              },
            ]}
          />

          <CodeSnippet
            language="java"
            title="Authentication Service Example"
            description="Java authentication with BCrypt and JWT"
            code={`@Service
public class AuthenticationService {
    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt

    @Autowired
    private JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // 2. Verify password with BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // 3. Generate JWT token
        String token = jwtService.generateToken(user);

        // 4. Log session to audit table
        sessionAuditRepository.save(SessionAudit.builder()
            .user(user)
            .username(user.getEmail())
            .loginAt(Instant.now())
            .ipAddress(request.getIpAddress())
            .userAgent(request.getUserAgent())
            .build());

        // 5. Return token and user info
        return AuthResponse.builder()
            .token(token)
            .user(mapToUserResponse(user))
            .build();
    }
}`}
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 5: Tablespace, Profile & Session Management</h2>

          <ExplainerPanel
            title="How Oracle Session Management Works"
            variant="info"
            sections={[
              {
                heading: 'Tablespace',
                content:
                  'Oracle tablespaces are logical storage units. Each user has a default tablespace for permanent data and a temporary tablespace for sorting operations. This controls disk space allocation and I/O performance.',
              },
              {
                heading: 'Profile',
                content:
                  'Database profiles enforce resource limits and password policies (max sessions, CPU time, password expiry, failed login attempts). Prevents resource exhaustion and enforces security policies.',
              },
              {
                heading: 'Session Tracking',
                content:
                  'Every login creates a session record with CLIENT_IDENTIFIER, used by VPD for row-level security. Sessions are audited to track user activity and detect unauthorized access.',
              },
            ]}
          />

          <CodeSnippet
            language="sql"
            title="Tablespace and Profile Setup"
            description="Creating tablespaces and user profiles in Oracle"
            code={`-- Create tablespace for application data
CREATE TABLESPACE shoestore_data
  DATAFILE 'shoestore_data01.dbf' SIZE 100M
  AUTOEXTEND ON NEXT 10M MAXSIZE 2G
  EXTENT MANAGEMENT LOCAL
  SEGMENT SPACE MANAGEMENT AUTO;

-- Create profile with resource limits
CREATE PROFILE app_user_profile LIMIT
  SESSIONS_PER_USER 5
  CPU_PER_SESSION UNLIMITED
  IDLE_TIME 30
  CONNECT_TIME UNLIMITED
  FAILED_LOGIN_ATTEMPTS 3
  PASSWORD_LIFE_TIME 90
  PASSWORD_REUSE_TIME 365
  PASSWORD_GRACE_TIME 7;

-- Create user with tablespace and profile
CREATE USER shoestore_app
  IDENTIFIED BY secure_password
  DEFAULT TABLESPACE shoestore_data
  TEMPORARY TABLESPACE temp
  PROFILE app_user_profile
  QUOTA UNLIMITED ON shoestore_data;`}
          />

          <div className="bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm">
            <h3 className="text-lg font-semibold text-zinc-900 mb-4">Current Session Context</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-3">
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Application Username</div>
                  <div className="text-zinc-900 font-semibold">{securityContext?.applicationUsername}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Database User</div>
                  <div className="text-zinc-900 font-mono text-sm">{securityContext?.dbUser}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Session User</div>
                  <div className="text-zinc-900 font-mono text-sm">{securityContext?.sessionUser}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Client Identifier</div>
                  <div className="text-zinc-900 font-mono text-sm">{securityContext?.clientIdentifier}</div>
                </div>
              </div>
              <div className="space-y-3">
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Database Name</div>
                  <div className="text-zinc-900 font-semibold">{securityContext?.dbName}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Database Host</div>
                  <div className="text-zinc-900">{securityContext?.dbHost}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Default Tablespace</div>
                  <div className="text-zinc-900 font-semibold">{securityContext?.defaultTablespace}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Temporary Tablespace</div>
                  <div className="text-zinc-900">{securityContext?.temporaryTablespace}</div>
                </div>
                <div>
                  <div className="text-xs text-zinc-500 mb-1">Profile Name</div>
                  <div className="text-zinc-900 font-semibold">{securityContext?.profileName}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Session Audit Logs</h2>

          <FilterBar
            searchPlaceholder="Search by username, IP, or DB user..."
            searchValue={searchTerm}
            onSearchChange={setSearchTerm}
          />

          <AuditTable
            columns={sessionColumns}
            data={filteredSessions}
            isLoading={isLoading}
            emptyMessage="No session audit logs found"
            maxHeight="500px"
          />
        </div>
      </div>
    </section>
  );
}
