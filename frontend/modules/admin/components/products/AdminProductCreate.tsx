import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { productAdminService } from '@/common/api/services/productAdminService';
import { productService } from '@/common/api/services/productService';
import {
  ProductAdminCreateRequest,
  ProductVariantAdminRequest,
  ProductImageAdminRequest,
  BackendCategory,
} from '@/common/types/backend';
import { BiLoaderAlt } from 'react-icons/bi';
import { AiOutlinePlus, AiOutlineClose } from 'react-icons/ai';

const AdminProductCreate = () => {
  const router = useRouter();

  const [categories, setCategories] = useState<BackendCategory[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Product basic info
  const [productName, setProductName] = useState('');
  const [slug, setSlug] = useState('');
  const [categoryId, setCategoryId] = useState<number | ''>('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState<number | ''>('');
  const [promotionPrice, setPromotionPrice] = useState<number | ''>('');

  // Variants and images
  const [variants, setVariants] = useState<ProductVariantAdminRequest[]>([]);
  const [images, setImages] = useState<ProductImageAdminRequest[]>([]);

  // Validation errors
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Fetch categories
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

  // Auto-generate slug from product name
  const handleProductNameChange = (value: string) => {
    setProductName(value);
    const generatedSlug = value
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '');
    setSlug(generatedSlug);
  };

  const addVariant = () => {
    setVariants([
      ...variants,
      {
        sizeValue: '',
        colorName: '',
        sku: '',
        styleColor: '',
        stockQty: 0,
        priceOverride: null,
        isActive: 1,
      },
    ]);
  };

  const removeVariant = (index: number) => {
    setVariants(variants.filter((_, i) => i !== index));
  };

  const updateVariant = (index: number, field: keyof ProductVariantAdminRequest, value: any) => {
    const updated = [...variants];
    updated[index] = { ...updated[index], [field]: value };
    setVariants(updated);
  };

  const addImage = () => {
    setImages([
      ...images,
      {
        imageUrl: '',
        displayOrder: images.length + 1,
        styleColor: '',
      },
    ]);
  };

  const removeImage = (index: number) => {
    setImages(images.filter((_, i) => i !== index));
  };

  const updateImage = (index: number, field: keyof ProductImageAdminRequest, value: any) => {
    const updated = [...images];
    updated[index] = { ...updated[index], [field]: value };
    setImages(updated);
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!productName.trim()) newErrors.productName = 'Product name is required';
    if (!slug.trim()) newErrors.slug = 'Slug is required';
    if (!categoryId) newErrors.categoryId = 'Category is required';
    if (!price || price <= 0) newErrors.price = 'Valid price is required';
    if (promotionPrice && promotionPrice < 0) newErrors.promotionPrice = 'Promotion price must be positive';
    if (variants.length === 0) newErrors.variants = 'At least one variant is required';
    if (images.length === 0) newErrors.images = 'At least one image is required';

    // Validate variants
    variants.forEach((variant, index) => {
      if (!variant.sizeValue.trim()) newErrors[`variant_${index}_size`] = 'Size is required';
      if (!variant.colorName.trim()) newErrors[`variant_${index}_color`] = 'Color name is required';
      if (!variant.sku.trim()) newErrors[`variant_${index}_sku`] = 'SKU is required';
      if (!variant.styleColor.trim()) newErrors[`variant_${index}_styleColor`] = 'Style color is required';
      if (variant.stockQty < 0) newErrors[`variant_${index}_stock`] = 'Stock cannot be negative';
    });

    // Validate images
    images.forEach((image, index) => {
      if (!image.imageUrl.trim()) newErrors[`image_${index}_url`] = 'Image URL is required';
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      setError('Please fix the validation errors');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const data: ProductAdminCreateRequest = {
        productName,
        slug,
        categoryId: categoryId as number,
        description: description || undefined,
        price: price as number,
        promotionPrice: promotionPrice || null,
        variants,
        images,
      };

      await productAdminService.createProduct(data);
      router.push('/admin/products');
    } catch (err: any) {
      setError(err.message || 'Failed to create product');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto max-w-5xl">
        <div className="mb-8 space-y-4">
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Product Management</p>
          <h1 className="text-5xl font-bold">Create Product</h1>
        </div>

        {error && (
          <div className="mb-6 rounded-3xl border border-red-200 bg-red-50 p-4 text-red-600">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Basic Information */}
          <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-xl font-semibold">Basic Information</h2>

            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <label className="mb-2 block text-sm font-semibold text-zinc-700">
                  Product Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={productName}
                  onChange={(e) => handleProductNameChange(e.target.value)}
                  className="input w-full"
                  maxLength={100}
                />
                {errors.productName && (
                  <p className="mt-1 text-sm text-red-500">{errors.productName}</p>
                )}
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-zinc-700">
                  Slug <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={slug}
                  onChange={(e) => setSlug(e.target.value)}
                  className="input w-full"
                  maxLength={150}
                />
                {errors.slug && <p className="mt-1 text-sm text-red-500">{errors.slug}</p>}
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-zinc-700">
                  Category <span className="text-red-500">*</span>
                </label>
                <select
                  value={categoryId}
                  onChange={(e) => setCategoryId(Number(e.target.value))}
                  className="input w-full"
                >
                  <option value="">Select category</option>
                  {categories.map((cat) => (
                    <option key={cat.categoryId} value={cat.categoryId}>
                      {cat.categoryName}
                    </option>
                  ))}
                </select>
                {errors.categoryId && <p className="mt-1 text-sm text-red-500">{errors.categoryId}</p>}
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-zinc-700">
                  Price (₫) <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  value={price}
                  onChange={(e) => setPrice(Number(e.target.value))}
                  className="input w-full"
                  min="0"
                  step="1000"
                />
                {errors.price && <p className="mt-1 text-sm text-red-500">{errors.price}</p>}
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-zinc-700">
                  Promotion Price (₫)
                </label>
                <input
                  type="number"
                  value={promotionPrice}
                  onChange={(e) => setPromotionPrice(Number(e.target.value))}
                  className="input w-full"
                  min="0"
                  step="1000"
                />
                {errors.promotionPrice && (
                  <p className="mt-1 text-sm text-red-500">{errors.promotionPrice}</p>
                )}
              </div>

              <div className="md:col-span-2">
                <label className="mb-2 block text-sm font-semibold text-zinc-700">Description</label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="input w-full"
                  rows={4}
                />
              </div>
            </div>
          </div>

          {/* Variants */}
          <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-semibold">Product Variants</h2>
              <button
                type="button"
                onClick={addVariant}
                className="btn flex items-center gap-2 px-4 py-2 text-sm"
              >
                <AiOutlinePlus size={16} />
                Add Variant
              </button>
            </div>

            {errors.variants && <p className="mb-4 text-sm text-red-500">{errors.variants}</p>}

            <div className="space-y-4">
              {variants.map((variant, index) => (
                <div key={index} className="rounded-xl border border-zinc-200 p-4">
                  <div className="mb-3 flex items-center justify-between">
                    <h3 className="font-semibold text-zinc-700">Variant {index + 1}</h3>
                    <button
                      type="button"
                      onClick={() => removeVariant(index)}
                      className="text-red-500 hover:text-red-700"
                    >
                      <AiOutlineClose size={20} />
                    </button>
                  </div>

                  <div className="grid gap-3 md:grid-cols-3">
                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Size <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={variant.sizeValue}
                        onChange={(e) => updateVariant(index, 'sizeValue', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={10}
                      />
                      {errors[`variant_${index}_size`] && (
                        <p className="mt-1 text-xs text-red-500">{errors[`variant_${index}_size`]}</p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Color Name <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={variant.colorName}
                        onChange={(e) => updateVariant(index, 'colorName', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={250}
                      />
                      {errors[`variant_${index}_color`] && (
                        <p className="mt-1 text-xs text-red-500">{errors[`variant_${index}_color`]}</p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        SKU <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={variant.sku}
                        onChange={(e) => updateVariant(index, 'sku', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={250}
                      />
                      {errors[`variant_${index}_sku`] && (
                        <p className="mt-1 text-xs text-red-500">{errors[`variant_${index}_sku`]}</p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Style Color <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={variant.styleColor}
                        onChange={(e) => updateVariant(index, 'styleColor', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={50}
                        placeholder="e.g., DM9537-100"
                      />
                      {errors[`variant_${index}_styleColor`] && (
                        <p className="mt-1 text-xs text-red-500">
                          {errors[`variant_${index}_styleColor`]}
                        </p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Stock Quantity
                      </label>
                      <input
                        type="number"
                        value={variant.stockQty}
                        onChange={(e) => updateVariant(index, 'stockQty', Number(e.target.value))}
                        className="input w-full text-sm"
                        min="0"
                      />
                      {errors[`variant_${index}_stock`] && (
                        <p className="mt-1 text-xs text-red-500">{errors[`variant_${index}_stock`]}</p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Price Override (₫)
                      </label>
                      <input
                        type="number"
                        value={variant.priceOverride || ''}
                        onChange={(e) =>
                          updateVariant(
                            index,
                            'priceOverride',
                            e.target.value ? Number(e.target.value) : null
                          )
                        }
                        className="input w-full text-sm"
                        min="0"
                        step="1000"
                      />
                    </div>
                  </div>

                  <div className="mt-3">
                    <label className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={variant.isActive === 1}
                        onChange={(e) => updateVariant(index, 'isActive', e.target.checked ? 1 : 0)}
                        className="rounded"
                      />
                      <span className="text-sm font-semibold text-zinc-600">Active</span>
                    </label>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Images */}
          <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-semibold">Product Images</h2>
              <button
                type="button"
                onClick={addImage}
                className="btn flex items-center gap-2 px-4 py-2 text-sm"
              >
                <AiOutlinePlus size={16} />
                Add Image
              </button>
            </div>

            {errors.images && <p className="mb-4 text-sm text-red-500">{errors.images}</p>}

            <div className="space-y-3">
              {images.map((image, index) => (
                <div key={index} className="rounded-xl border border-zinc-200 p-4">
                  <div className="mb-2 flex items-center justify-between">
                    <span className="text-xs font-semibold text-zinc-600">Image {index + 1}</span>
                    <button
                      type="button"
                      onClick={() => removeImage(index)}
                      className="text-red-500 hover:text-red-700"
                    >
                      <AiOutlineClose size={20} />
                    </button>
                  </div>

                  <div className="grid gap-3 md:grid-cols-3">
                    <div className="md:col-span-2">
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Image URL <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={image.imageUrl}
                        onChange={(e) => updateImage(index, 'imageUrl', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={500}
                        placeholder="https://example.com/image.jpg"
                      />
                      {errors[`image_${index}_url`] && (
                        <p className="mt-1 text-xs text-red-500">{errors[`image_${index}_url`]}</p>
                      )}
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Style Color
                      </label>
                      <input
                        type="text"
                        value={image.styleColor || ''}
                        onChange={(e) => updateImage(index, 'styleColor', e.target.value)}
                        className="input w-full text-sm"
                        maxLength={50}
                        placeholder="DM9537-100"
                      />
                    </div>

                    <div>
                      <label className="mb-1 block text-xs font-semibold text-zinc-600">
                        Display Order
                      </label>
                      <input
                        type="number"
                        value={image.displayOrder}
                        onChange={(e) => updateImage(index, 'displayOrder', Number(e.target.value))}
                        className="input w-full text-sm"
                        min="1"
                      />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => router.push('/admin/products')}
              className="flex-1 rounded-full border border-zinc-200 px-6 py-3 font-semibold transition hover:border-zinc-900"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn flex-1 flex items-center justify-center gap-2"
            >
              {loading && <BiLoaderAlt className="animate-spin" size={20} />}
              {loading ? 'Creating...' : 'Create Product'}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default AdminProductCreate;
