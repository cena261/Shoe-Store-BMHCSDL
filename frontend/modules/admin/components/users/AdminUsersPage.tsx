import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { userAdminService } from '@/common/api/services/userAdminService';
import {
  UserAdminResponse,
  UserAdminPageResponse,
} from '@/common/types/backend';
import { AiOutlineSearch, AiOutlineEye, AiOutlineDelete, AiOutlineArrowLeft } from 'react-icons/ai';
import { BiLoaderAlt } from 'react-icons/bi';
import { useModal } from '@/common/recoil/modal';
import { useToast } from '@/common/recoil/toast/toast.hooks';

const AdminUsersPage = () => {
  const router = useRouter();
  const { openModal, closeModal } = useModal();
  const toast = useToast();

  const [users, setUsers] = useState<UserAdminResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState('');
  const [activeFilter, setActiveFilter] = useState<number | ''>('');

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 15;

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);

      const response: UserAdminPageResponse = await userAdminService.getUsers({
        search: search || undefined,
        isActive: activeFilter !== '' ? activeFilter : undefined,
        page: currentPage,
        pageSize,
      });

      setUsers(response.users);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [currentPage, activeFilter]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    fetchUsers();
  };

  const handleClearSearch = () => {
    setSearch('');
    setCurrentPage(1);
    setTimeout(fetchUsers, 0);
  };

  const handleToggleActive = async (user: UserAdminResponse) => {
    const newActiveStatus = user.isActive === 1 ? 0 : 1;
    const actionText = newActiveStatus === 1 ? 'activate' : 'deactivate';

    openModal(
      <div className="rounded-3xl bg-white p-8">
        <h2 className="text-2xl font-bold">
          {newActiveStatus === 1 ? 'Activate' : 'Deactivate'} User?
        </h2>
        <p className="mt-4 text-zinc-600">
          Are you sure you want to {actionText} "{user.fullName}" ({user.email})?
        </p>
        <div className="mt-6 flex gap-4">
          <button
            onClick={closeModal}
            className="flex-1 rounded-full border border-zinc-200 px-6 py-3 font-semibold transition hover:border-zinc-900"
          >
            Cancel
          </button>
          <button
            onClick={async () => {
              try {
                await userAdminService.setUserActive(
                  user.userId,
                  newActiveStatus === 1
                );
                closeModal();
                toast.success(`User ${newActiveStatus === 1 ? 'activated' : 'deactivated'} successfully`);
                fetchUsers();
              } catch (err: any) {
                toast.error(err.message || `Failed to ${actionText} user`);
              }
            }}
            className="flex-1 rounded-full bg-black px-6 py-3 font-semibold text-white transition hover:scale-105"
          >
            {newActiveStatus === 1 ? 'Activate' : 'Deactivate'}
          </button>
        </div>
      </div>
    );
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const hasAdminRole = (roles: string[]) => {
    return roles.includes('Admin');
  };

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-8">
        <div className="space-y-4">
          <button
            onClick={() => router.push('/admin')}
            className="flex items-center gap-2 text-zinc-600 transition hover:text-zinc-900"
          >
            <AiOutlineArrowLeft size={20} />
            <span className="font-semibold">Back to Admin Dashboard</span>
          </button>
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">User Management</p>
          <h1 className="text-5xl font-bold">Users</h1>
        </div>

        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <form onSubmit={handleSearch} className="flex flex-1 gap-2">
            <div className="relative flex-1 max-w-md">
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by email, name, or phone..."
                className="input w-full pr-10"
              />
              <AiOutlineSearch className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400" />
            </div>
            <button type="submit" className="btn px-6">
              Search
            </button>
            {search && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="rounded-full border border-zinc-200 px-6 py-2 font-semibold transition hover:border-zinc-900"
              >
                Clear
              </button>
            )}
          </form>

          <div className="flex items-center gap-2">
            <label className="text-sm font-semibold text-zinc-700">Status:</label>
            <select
              value={activeFilter}
              onChange={(e) => {
                setActiveFilter(e.target.value ? Number(e.target.value) : '');
                setCurrentPage(1);
              }}
              className="input"
            >
              <option value="">All Status</option>
              <option value="1">Active</option>
              <option value="0">Inactive</option>
            </select>
          </div>
        </div>

        {error && (
          <div className="rounded-3xl border border-red-200 bg-red-50 p-4 text-center text-red-600">
            {error}
          </div>
        )}

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <BiLoaderAlt className="animate-spin text-zinc-400" size={48} />
          </div>
        ) : users.length === 0 ? (
          <div className="rounded-3xl border border-zinc-200 bg-white p-12 text-center">
            <p className="text-lg text-zinc-500">No users found</p>
          </div>
        ) : (
          <>
            <div className="rounded-3xl border border-zinc-200 bg-white shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="border-b border-zinc-200 bg-zinc-50">
                    <tr>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">User</th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Email</th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Phone</th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Roles</th>
                      <th className="px-6 py-4 text-center text-sm font-semibold text-zinc-700">Status</th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Joined</th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Last Login</th>
                      <th className="px-6 py-4 text-center text-sm font-semibold text-zinc-700">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((user) => (
                      <tr
                        key={user.userId}
                        className="border-b border-zinc-100 hover:bg-zinc-50 transition"
                      >
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-3">
                            <img
                              src={`https://ui-avatars.com/api/?name=${encodeURIComponent(user.fullName)}&background=066fd1&color=fff&size=40`}
                              alt={user.fullName}
                              className="h-10 w-10 rounded-full"
                            />
                            <div>
                              <p className="font-semibold text-zinc-900">{user.fullName}</p>
                              <p className="text-sm text-zinc-500">ID: {user.userId}</p>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-zinc-700">{user.email}</td>
                        <td className="px-6 py-4 text-zinc-700">{user.phone || '-'}</td>
                        <td className="px-6 py-4">
                          <div className="flex flex-wrap gap-1">
                            {user.roles && user.roles.length > 0 ? (
                              user.roles.map((role) => (
                                <span
                                  key={role}
                                  className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${
                                    role === 'Admin'
                                      ? 'bg-red-100 text-red-600'
                                      : 'bg-blue-100 text-blue-600'
                                  }`}
                                >
                                  {role}
                                </span>
                              ))
                            ) : (
                              <span className="text-xs text-zinc-400">No roles</span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <span
                            className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                              user.isActive === 1
                                ? 'bg-green-100 text-green-600'
                                : 'bg-red-100 text-red-600'
                            }`}
                          >
                            {user.isActive === 1 ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-zinc-700">{formatDate(user.createdAt)}</td>
                        <td className="px-6 py-4 text-zinc-700">{formatDate(user.lastLogin)}</td>
                        <td className="px-6 py-4">
                          <div className="flex items-center justify-center gap-2">
                            <button
                              onClick={() => router.push(`/admin/users/${user.userId}`)}
                              className="rounded-full bg-cyan-500 p-2 text-white transition hover:bg-cyan-600"
                              title="View Details"
                            >
                              <AiOutlineEye size={16} />
                            </button>
                            <button
                              onClick={() => handleToggleActive(user)}
                              className={`rounded-full p-2 text-white transition ${
                                user.isActive === 1
                                  ? 'bg-zinc-500 hover:bg-zinc-600'
                                  : 'bg-green-500 hover:bg-green-600'
                              }`}
                              title={user.isActive === 1 ? 'Deactivate' : 'Activate'}
                            >
                              <AiOutlineDelete size={16} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {totalPages > 1 && (
              <div className="flex items-center justify-between">
                <p className="text-sm text-zinc-600">
                  Showing {(currentPage - 1) * pageSize + 1} to{' '}
                  {Math.min(currentPage * pageSize, totalElements)} of {totalElements} users
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
                    disabled={currentPage === 1}
                    className="rounded-full border border-zinc-200 px-4 py-2 font-semibold transition hover:border-zinc-900 disabled:opacity-50 disabled:hover:border-zinc-200"
                  >
                    Previous
                  </button>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <button
                      key={page}
                      onClick={() => setCurrentPage(page)}
                      className={`rounded-full px-4 py-2 font-semibold transition ${
                        currentPage === page
                          ? 'bg-black text-white'
                          : 'border border-zinc-200 hover:border-zinc-900'
                      }`}
                    >
                      {page}
                    </button>
                  ))}
                  <button
                    onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
                    disabled={currentPage === totalPages}
                    className="rounded-full border border-zinc-200 px-4 py-2 font-semibold transition hover:border-zinc-900 disabled:opacity-50 disabled:hover:border-zinc-200"
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </section>
  );
};

export default AdminUsersPage;
