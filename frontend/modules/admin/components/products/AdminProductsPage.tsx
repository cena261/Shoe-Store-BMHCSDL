import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { productAdminService } from '@/common/api/services/productAdminService';
import { productService } from '@/common/api/services/productService';
import {
  ProductAdminResponse,
  ProductAdminPageResponse,
  BackendCategory,
} from '@/common/types/backend';
import { AiOutlinePlus, AiOutlineSearch, AiOutlineEdit, AiOutlineDelete, AiOutlineArrowLeft } from 'react-icons/ai';
import { BiLoaderAlt } from 'react-icons/bi';
import { useModal } from '@/common/recoil/modal';
import { useToast } from '@/common/recoil/toast/toast.hooks';

const AdminProductsPage = () => {
  const router = useRouter();
  const { openModal, closeModal } = useModal();
  const toast = useToast();

  const [products, setProducts] = useState<ProductAdminResponse[]>([]);
  const [categories, setCategories] = useState<BackendCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<number | ''>('');
  const [activeFilter, setActiveFilter] = useState<number | ''>('');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 15;

  // Fetch categories on mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const cats = await productService.getCategories();
        setCategories(cats);
      } catch (err) {
        console.error('Failed to fetch categories:', err);
      }
    };
    fetchCategories();
  }, []);

  // Fetch products
  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);

      const response: ProductAdminPageResponse = await productAdminService.getProducts({
        search: search || undefined,
        categoryId: categoryFilter || undefined,
        isActive: activeFilter !== '' ? activeFilter : undefined,
        page: currentPage,
        pageSize,
      });

      setProducts(response.products);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setCurrentPage(response.currentPage);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [currentPage, categoryFilter, activeFilter]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    fetchProducts();
  };

  const handleClearSearch = () => {
    setSearch('');
    setCurrentPage(1);
    setTimeout(fetchProducts, 0);
  };

  const handleToggleActive = async (product: ProductAdminResponse) => {
    const newActiveStatus = product.isActive === 1 ? 0 : 1;
    const actionText = newActiveStatus === 1 ? 'activate' : 'deactivate';

    openModal(
      <div className="rounded-3xl bg-white p-8">
        <h2 className="text-2xl font-bold">
          {newActiveStatus === 1 ? 'Activate' : 'Deactivate'} Product?
        </h2>
        <p className="mt-4 text-zinc-600">
          Are you sure you want to {actionText} "{product.productName}"?
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
                await productAdminService.setProductActive(
                  product.productId,
                  newActiveStatus === 1
                );
                closeModal();
                toast.success(`Product ${newActiveStatus === 1 ? 'activated' : 'deactivated'} successfully`);
                fetchProducts();
              } catch (err: any) {
                toast.error(err.message || `Failed to ${actionText} product`);
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

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getTotalStock = (product: ProductAdminResponse) => {
    return product.variants?.reduce((sum, v) => sum + v.stockQty, 0) || 0;
  };

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-8">
        {/* Header */}
        <div className="space-y-4">
          <button
            onClick={() => router.push('/admin')}
            className="flex items-center gap-2 text-zinc-600 transition hover:text-zinc-900"
          >
            <AiOutlineArrowLeft size={20} />
            <span className="font-semibold">Back to Admin Dashboard</span>
          </button>
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Product Management</p>
          <h1 className="text-5xl font-bold">Products</h1>
        </div>

        {/* Filters and Actions */}
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <form onSubmit={handleSearch} className="flex flex-1 gap-2">
            <div className="relative flex-1 max-w-md">
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search products..."
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

          <button
            onClick={() => router.push('/admin/products/create')}
            className="btn flex items-center gap-2 px-6"
          >
            <AiOutlinePlus size={20} />
            Create Product
          </button>
        </div>

        {/* Category and Active Filters */}
        <div className="flex flex-wrap gap-4">
          <div className="flex items-center gap-2">
            <label className="text-sm font-semibold text-zinc-700">Category:</label>
            <select
              value={categoryFilter}
              onChange={(e) => {
                setCategoryFilter(e.target.value ? Number(e.target.value) : '');
                setCurrentPage(1);
              }}
              className="input"
            >
              <option value="">All Categories</option>
              {categories.map((cat) => (
                <option key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryName}
                </option>
              ))}
            </select>
          </div>

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

        {/* Loading State */}
        {loading && (
          <div className="flex justify-center py-20">
            <BiLoaderAlt className="animate-spin text-4xl text-zinc-400" />
          </div>
        )}

        {/* Error State */}
        {error && !loading && (
          <div className="rounded-3xl border border-red-200 bg-red-50 p-6 text-center text-red-600">
            {error}
          </div>
        )}

        {/* Product Table */}
        {!loading && !error && (
          <>
            <div className="overflow-x-auto rounded-3xl border border-zinc-200 bg-white shadow-sm">
              <table className="w-full">
                <thead className="border-b border-zinc-200 bg-zinc-50">
                  <tr>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Image</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Product</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Category</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Price</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Variants</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Stock</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Status</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Created</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {products.length === 0 ? (
                    <tr>
                      <td colSpan={9} className="px-6 py-20 text-center text-zinc-500">
                        No products found
                      </td>
                    </tr>
                  ) : (
                    products.map((product) => {
                      const totalStock = getTotalStock(product);
                      const mainImage = product.images?.find((img) => img.displayOrder === 1);

                      return (
                        <tr key={product.productId} className="border-b border-zinc-100 hover:bg-zinc-50">
                          <td className="px-6 py-4">
                            {mainImage ? (
                              <img
                                src={mainImage.imageUrl}
                                alt={product.productName}
                                className="h-16 w-16 rounded-lg object-cover"
                              />
                            ) : (
                              <div className="h-16 w-16 rounded-lg bg-zinc-100 flex items-center justify-center text-zinc-400">
                                No Image
                              </div>
                            )}
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex flex-col">
                              <span className="font-semibold text-zinc-900">{product.productName}</span>
                              <span className="text-sm text-zinc-500">{product.slug}</span>
                            </div>
                          </td>
                          <td className="px-6 py-4 text-zinc-700">{product.categoryName}</td>
                          <td className="px-6 py-4">
                            <div className="flex flex-col">
                              {product.promotionPrice ? (
                                <>
                                  <span className="font-semibold text-zinc-900">
                                    {formatPrice(product.promotionPrice)}
                                  </span>
                                  <span className="text-sm text-zinc-500 line-through">
                                    {formatPrice(product.price)}
                                  </span>
                                </>
                              ) : (
                                <span className="font-semibold text-zinc-900">
                                  {formatPrice(product.price)}
                                </span>
                              )}
                            </div>
                          </td>
                          <td className="px-6 py-4 text-center text-zinc-700">{product.variants?.length || 0}</td>
                          <td className="px-6 py-4">
                            <span
                              className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                                totalStock > 0
                                  ? 'bg-green-100 text-green-600'
                                  : 'bg-red-100 text-red-600'
                              }`}
                            >
                              {totalStock}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span
                              className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                                product.isActive === 1
                                  ? 'bg-green-100 text-green-600'
                                  : 'bg-zinc-100 text-zinc-600'
                              }`}
                            >
                              {product.isActive === 1 ? 'Active' : 'Inactive'}
                            </span>
                          </td>
                          <td className="px-6 py-4 text-zinc-700">{formatDate(product.createdAt)}</td>
                          <td className="px-6 py-4">
                            <div className="flex gap-2">
                              <button
                                onClick={() => router.push(`/admin/products/${product.productId}/edit`)}
                                className="rounded-full bg-blue-500 p-2 text-white transition hover:bg-blue-600"
                                title="Edit"
                              >
                                <AiOutlineEdit size={16} />
                              </button>
                              <button
                                onClick={() => handleToggleActive(product)}
                                className={`rounded-full p-2 text-white transition ${
                                  product.isActive === 1
                                    ? 'bg-zinc-500 hover:bg-zinc-600'
                                    : 'bg-green-500 hover:bg-green-600'
                                }`}
                                title={product.isActive === 1 ? 'Deactivate' : 'Activate'}
                              >
                                <AiOutlineDelete size={16} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex flex-col items-center gap-4 sm:flex-row sm:justify-between">
                <p className="text-sm text-zinc-600">
                  Showing {(currentPage - 1) * pageSize + 1} to{' '}
                  {Math.min(currentPage * pageSize, totalElements)} of {totalElements} products
                </p>

                <div className="flex gap-2">
                  <button
                    onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                    disabled={currentPage === 1}
                    className="rounded-full border border-zinc-200 px-4 py-2 font-semibold transition hover:border-zinc-900 disabled:opacity-30 disabled:hover:border-zinc-200"
                  >
                    Previous
                  </button>

                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    const pageNum = Math.max(1, currentPage - 2) + i;
                    if (pageNum > totalPages) return null;

                    return (
                      <button
                        key={pageNum}
                        onClick={() => setCurrentPage(pageNum)}
                        className={`rounded-full px-4 py-2 font-semibold transition ${
                          currentPage === pageNum
                            ? 'bg-black text-white'
                            : 'border border-zinc-200 hover:border-zinc-900'
                        }`}
                      >
                        {pageNum}
                      </button>
                    );
                  })}

                  <button
                    onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                    disabled={currentPage === totalPages}
                    className="rounded-full border border-zinc-200 px-4 py-2 font-semibold transition hover:border-zinc-900 disabled:opacity-30 disabled:hover:border-zinc-200"
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

export default AdminProductsPage;
