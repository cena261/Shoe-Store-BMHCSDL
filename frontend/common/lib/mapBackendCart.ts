import type { CartItemResponse } from '@/common/types/backend';

export const mapBackendCartToFrontend = (
  backendCartItems: CartItemResponse[]
): CartProduct[] => {
  return backendCartItems.map((item) => ({
    id: item.variantId.toString(),
    quantity: item.quantity,
    size: parseInt(item.sizeValue),
    cartId: item.cartId,
    attributes: {
      name: item.productName,
      price: item.price,
      category: item.category.toLowerCase(),
      slug: item.slug,
      color: item.colorName,
      promotionPrice: item.promotionPrice,
      images: {
        data: [
          {
            id: `${item.variantId}-image`,
            attributes: {
              width: 800,
              height: 800,
              hash: item.imageUrl,
            },
          },
        ],
      },
    },
  }));
};
