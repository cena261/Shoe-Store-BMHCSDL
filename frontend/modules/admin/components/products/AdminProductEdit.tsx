import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { productAdminService } from '@/common/api/services/productAdminService';
import { productService } from '@/common/api/services/productService';
import {
  ProductAdminUpdateRequest,
  ProductAdminResponse,
  ProductVariantAdminRequest,
  ProductImageAdminRequest,
  BackendCategory,
} from '@/common/types/backend';
import { BiLoaderAlt } from 'react-icons/bi';
import { AiOutlinePlus, AiOutlineClose } from 'react-icons/ai';

const AdminProductEdit = () => {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<ProductAdminResponse | null>(null);
  const [categories, setCategories] = useState<BackendCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
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

  // Fetch product and categories
  useEffect(() => {
    if (!id) return;

    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [productData, cats] = await Promise.all([
          productAdminService.getProductById(Number(id)),
          productService.getCategories(),
        ]);

        setProduct(productData);
        setCategories(cats);

        // Populate form
        setProductName(productData.productName);
        setSlug(productData.slug);
        setCategoryId(productData.categoryId);
        setDescription(productData.description || '');
        setPrice(productData.price);
        setPromotionPrice(productData.promotionPrice || '');

        // Convert response variants to request format
        setVariants(
          productData.variants.map((v) => ({
            variantId: v.variantId,
            sizeValue: v.sizeValue,
            colorName: v.colorName,
            sku: v.sku,
            styleColor: v.styleColor,
            stockQty: v.stockQty,
            priceOverride: v.priceOverride,
            isActive: v.isActive,
          }))
        );

        // Convert response images to request format
        setImages(
          productData.images.map((img) => ({
            imageId: img.imageId,
            imageUrl: img.imageUrl,
            displayOrder: img.displayOrder,
            styleColor: img.styleColor,
          }))
        );
      } catch (err: any) {
        setError(err.message || 'Failed to load product');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const addVariant = () => {
    setVariants([
      ...variants,
      {
        variantId: null,
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
        imageId: null,
        imageUrl: '',
        displayOrder: Math.max(0, ...images.map((img) => img.displayOrder)) + 1,
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
      setSaving(true);
      setError(null);

      const data: ProductAdminUpdateRequest = {
        productName,
        slug,
        categoryId: categoryId as number,
        description: description || undefined,
        price: price as number,
        promotionPrice: promotionPrice || null,
        variants,
        images,
      };

      await productAdminService.updateProduct(Number(id), data);
      router.push('/admin/products');
    } catch (err: any) {
      setError(err.message || 'Failed to update product');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <BiLoaderAlt className="animate-spin text-4xl text-zinc-400" />
      </div>
    );
  }

  if (error && !product) {
    return (
      <div className="flex min-h-screen items-center justify-center p-4">
        <div className="rounded-3xl border border-red-200 bg-red-50 p-6 text-center text-red-600">
          {error}
        </div>
      </div>
    );
  }

  // Group variants by color for better UX (similar to old ASP.NET)
  const colorGroups = variants.reduce((groups, variant) => {
    const key = `${variant.colorName}-${variant.styleColor}`;
    if (!groups[key]) {
      groups[key] = {
        colorName: variant.colorName,
        styleColor: variant.styleColor,
        variants: [],
      };
    }
    groups[key].variants.push(variant);
    return groups;
  }, {} as Record<string, { colorName: string; styleColor: string; variants: ProductVariantAdminRequest[] }>);

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto max-w-5xl">
        <div className="mb-8 space-y-4">
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Product Management</p>
          <h1 className="text-5xl font-bold">Edit Product</h1>
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
                  onChange={(e) => setProductName(e.target.value)}
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
                  onChange={(e) => setPromotionPrice(Number(e.target.value) || '')}
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

          {/* Variants - Color Grouped */}
          <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-semibold">Product Variants (Grouped by Color)</h2>
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

            <div className="space-y-6">
              {Object.entries(colorGroups).map(([key, group]) => {
                const mainImage = images.find((img) => img.displayOrder === 1);

                return (
                  <div key={key} className="rounded-xl border border-zinc-200 p-4">
                    <div className="mb-3 flex items-center gap-4">
                      {mainImage && (
                        <img
                          src={mainImage.imageUrl}
                          alt={group.colorName}
                          className="h-20 w-20 rounded-lg object-cover"
                        />
                      )}
                      <div>
                        <h3 className="font-semibold text-zinc-900">{group.colorName}</h3>
                        <p className="text-sm text-zinc-500">Style: {group.styleColor}</p>
                      </div>
                    </div>

                    <div className="space-y-3">
                      {group.variants.map((variant) => {
                        const index = variants.indexOf(variant);

                        return (
                          <div key={index} className="grid gap-3 rounded-lg bg-zinc-50 p-3 md:grid-cols-4">
                            <div>
                              <label className="mb-1 block text-xs font-semibold text-zinc-600">
                                Size
                              </label>
                              <input
                                type="text"
                                value={variant.sizeValue}
                                onChange={(e) => updateVariant(index, 'sizeValue', e.target.value)}
                                className="input w-full text-sm"
                                maxLength={10}
                              />
                            </div>

                            <div>
                              <label className="mb-1 block text-xs font-semibold text-zinc-600">
                                SKU
                              </label>
                              <input
                                type="text"
                                value={variant.sku}
                                onChange={(e) => updateVariant(index, 'sku', e.target.value)}
                                className="input w-full text-sm"
                              />
                            </div>

                            <div>
                              <label className="mb-1 block text-xs font-semibold text-zinc-600">
                                Stock
                              </label>
                              <input
                                type="number"
                                value={variant.stockQty}
                                onChange={(e) => updateVariant(index, 'stockQty', Number(e.target.value))}
                                className="input w-full text-sm"
                                min="0"
                              />
                            </div>

                            <div className="flex items-end gap-2">
                              <label className="flex items-center gap-2">
                                <input
                                  type="checkbox"
                                  checked={variant.isActive === 1}
                                  onChange={(e) => updateVariant(index, 'isActive', e.target.checked ? 1 : 0)}
                                  className="rounded"
                                />
                                <span className="text-xs font-semibold text-zinc-600">Active</span>
                              </label>
                              <button
                                type="button"
                                onClick={() => removeVariant(index)}
                                className="ml-auto text-red-500 hover:text-red-700"
                              >
                                <AiOutlineClose size={16} />
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Images - Grouped by Color */}
          <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-xl font-semibold">Product Images (Grouped by Color)</h2>

            {errors.images && <p className="mb-4 text-sm text-red-500">{errors.images}</p>}

            <div className="space-y-6">
              {Object.entries(colorGroups).map(([key, group]) => {
                const colorImages = images.filter(img => img.styleColor === group.styleColor);

                return (
                  <div key={key} className="rounded-xl border border-zinc-200 p-4">
                    <div className="mb-3 flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold text-zinc-900">{group.colorName}</h3>
                        <p className="text-sm text-zinc-500">Style: {group.styleColor}</p>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          const newImage: ProductImageAdminRequest = {
                            imageId: null,
                            imageUrl: '',
                            displayOrder: Math.max(0, ...images.map(img => img.displayOrder)) + 1,
                            styleColor: group.styleColor,
                          };
                          setImages([...images, newImage]);
                        }}
                        className="btn flex items-center gap-2 px-3 py-1 text-sm"
                      >
                        <AiOutlinePlus size={14} />
                        Add image to {group.styleColor}
                      </button>
                    </div>

                    {colorImages.length === 0 ? (
                      <p className="py-4 text-center text-sm text-zinc-500">
                        No images for this color yet
                      </p>
                    ) : (
                      <div className="grid gap-3 md:grid-cols-3">
                        {colorImages.map((image) => {
                          const index = images.indexOf(image);

                          return (
                            <div key={index} className="rounded-lg border border-zinc-200 p-3">
                              <div className="mb-2 flex items-center justify-between">
                                <span className="text-xs font-semibold text-zinc-600">
                                  Order: {image.displayOrder}
                                </span>
                                <button
                                  type="button"
                                  onClick={() => removeImage(index)}
                                  className="text-red-500 hover:text-red-700"
                                >
                                  <AiOutlineClose size={14} />
                                </button>
                              </div>

                              {image.imageUrl && (
                                <img
                                  src={image.imageUrl}
                                  alt={`${group.colorName} image`}
                                  className="mb-2 h-32 w-full rounded object-cover"
                                />
                              )}

                              <div className="space-y-2">
                                <div>
                                  <label className="mb-1 block text-xs font-semibold text-zinc-600">
                                    Image URL
                                  </label>
                                  <input
                                    type="text"
                                    value={image.imageUrl}
                                    onChange={(e) => updateImage(index, 'imageUrl', e.target.value)}
                                    className="input w-full text-xs"
                                    placeholder="https://..."
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
                                    className="input w-full text-xs"
                                    min="1"
                                  />
                                </div>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>
                );
              })}
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
              disabled={saving}
              className="btn flex-1 flex items-center justify-center gap-2"
            >
              {saving && <BiLoaderAlt className="animate-spin" size={20} />}
              {saving ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default AdminProductEdit;
