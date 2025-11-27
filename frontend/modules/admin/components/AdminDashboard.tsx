import { useRecoilValue } from 'recoil';

import userAtom, { useLogout } from '@/common/recoil/user';

const AdminDashboard = () => {
  const user = useRecoilValue(userAtom);
  const { handleLogout } = useLogout();

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-10">
        <div className="space-y-4">
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Admin Panel</p>
          <h1 className="text-5xl font-bold">Admin Dashboard</h1>
          <p className="text-base text-zinc-500">
            Welcome to the admin panel. Manage your store from here.
          </p>
        </div>

        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm">
            <h2 className="text-lg font-semibold">Admin Information</h2>
            <div className="mt-3 space-y-2">
              <p className="text-sm text-zinc-500">
                Name: <span className="font-medium text-zinc-900">{user.fullName || user.username}</span>
              </p>
              <p className="text-sm text-zinc-500">
                Email: <span className="font-medium text-zinc-900">{user.email}</span>
              </p>
              <p className="text-sm text-zinc-500">
                Role: <span className="font-medium text-zinc-900">{user.roles?.join(', ')}</span>
              </p>
            </div>
          </article>

          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm">
            <h2 className="text-lg font-semibold">Quick Actions</h2>
            <div className="mt-3 space-y-2">
              <button
                className="w-full rounded-full border border-zinc-200 px-4 py-2 text-sm font-semibold transition hover:border-zinc-900 hover:bg-zinc-900 hover:text-white"
                onClick={() => (window.location.href = '/')}
              >
                View Store
              </button>
              <button
                className="w-full rounded-full border border-zinc-200 px-4 py-2 text-sm font-semibold transition hover:border-zinc-900 hover:bg-zinc-900 hover:text-white"
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          </article>

          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm">
            <h2 className="text-lg font-semibold">System Status</h2>
            <div className="mt-3 space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm text-zinc-500">System</span>
                <span className="rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-600">
                  Operational
                </span>
              </div>
            </div>
          </article>
        </div>

        <div className="rounded-3xl border border-zinc-200 p-8 shadow-sm">
          <h2 className="text-2xl font-semibold">Coming Soon</h2>
          <p className="mt-2 text-zinc-500">
            Admin features will be implemented here:
          </p>
          <ul className="mt-4 space-y-2 text-zinc-600">
            <li>• User Management</li>
            <li>• Order Management</li>
            <li>• Product Management</li>
            <li>• Security & Analytics</li>
          </ul>
        </div>
      </div>
    </section>
  );
};

export default AdminDashboard;
