import type { BackendProduct } from '@/common/types/backend';

export const mapBackendProductToSimple = (
  product: BackendProduct
): SimpleProduct => {
  const mainImage =
    product.mainImageUrl ||
    product.images?.[0]?.imageUrl ||
    product.colorVariants?.[0]?.mainImageUrl ||
    '/placeholder.jpg';

  const colorVariants = product.colorVariants?.map((variant) => ({
    colorName: variant.colorName,
    mainImageUrl: variant.mainImageUrl,
    slug: `${product.slug}-${variant.styleColor}`,
    styleColor: variant.styleColor,
  }));

  return {
    id: product.productId.toString(),
    attributes: {
      name: product.productName,
      price: product.price,
      category: product.categoryName?.toLowerCase() || 'shoes',
      slug: product.slug,
      color: product.colorVariants?.[0]?.colorName || 'default',
      promotionPrice: product.promotionPrice || null,
      colorVariants,
      images: {
        data: [
          {
            id: product.images?.[0]?.imageId.toString() || '1',
            attributes: {
              width: 800,
              height: 1000,
              hash: mainImage,
            },
          },
        ],
      },
    },
  };
};

export const mapBackendProductsToSimple = (
  products: BackendProduct[]
): SimpleProduct[] => {
  return products.map(mapBackendProductToSimple);
};
