import { useState, useEffect } from 'react';

import Image from 'next/image';
import { useRouter } from 'next/router';

import { productService } from '@/common/api/services/productService';
import Spinner from '@/common/components/spinner/components/Spinner';
import { useAddToCart } from '@/common/recoil/cart/cart.hooks';
import type {
  ProductDetailResponse,
  ColorOption,
  SizeOption,
} from '@/common/types/backend';

const ProductDetailPage = () => {
  const router = useRouter();
  const { slug, styleColor } = router.query;
  const addToCart = useAddToCart();

  const [product, setProduct] = useState<ProductDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [selectedSize, setSelectedSize] = useState<number | null>(null);

  useEffect(() => {
    if (!router.isReady || !slug) {
      return;
    }

    const fetchProduct = async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await productService.getProductDetail(
          slug as string,
          styleColor as string | undefined
        );

        setProduct(data);
        setCurrentImageIndex(0);
        setSelectedSize(null);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load product details. Please try again later.'
        );
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [router.isReady, slug]);

  const handleColorChange = async (newStyleColor: string) => {
    if (newStyleColor === product?.selectedStyleColor || !slug) return;

    try {
      const data = await productService.getProductDetail(
        slug as string,
        newStyleColor
      );
      setProduct(data);
      setCurrentImageIndex(0);
      setSelectedSize(null);

      router.push(
        {
          pathname: router.pathname,
          query: { slug, styleColor: newStyleColor },
        },
        undefined,
        { shallow: true }
      );
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Failed to load product details. Please try again later.'
      );
    }
  };

  const handlePreviousImage = () => {
    if (!product) return;
    setCurrentImageIndex(
      (prev) => (prev - 1 + product.images.length) % product.images.length
    );
  };

  const handleNextImage = () => {
    if (!product) return;
    setCurrentImageIndex((prev) => (prev + 1) % product.images.length);
  };

  const handleAddToCart = () => {
    if (!selectedSize || !product) {
      return;
    }

    const selectedVariant = product.availableSizes.find(
      (s) => s.variantId === selectedSize
    );

    if (!selectedVariant) {
      return;
    }

    const simpleProduct: SimpleProduct = {
      id: product.productId.toString(),
      attributes: {
        name: product.productName,
        price: product.price,
        category: product.categoryName,
        slug: product.slug,
        color: product.selectedColorName,
        promotionPrice: product.promotionPrice,
        images: {
          data: product.images.map((url, index) => ({
            id: `${product.productId}-${index}`,
            attributes: {
              width: 800,
              height: 800,
              hash: url,
            },
          })),
        },
      },
    };

    addToCart(simpleProduct, parseInt(selectedVariant.sizeValue), selectedSize);
  };

  if (loading) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <div className="text-center">
          <h2 className="mb-4 text-2xl font-bold text-red-500">Error</h2>
          <p className="text-gray-600">{error || 'Product not found'}</p>
          <button onClick={() => router.push('/shoes')} className="btn mt-4">
            Back to Products
          </button>
        </div>
      </div>
    );
  }

  const currentImage =
    product.images[currentImageIndex] || '/placeholder.jpg';

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 pt-32 sm:px-6 lg:px-8">
      <div className="grid grid-cols-1 gap-8 lg:grid-cols-2">
        <div className="flex flex-col gap-4">
          <div className="group relative aspect-square overflow-hidden rounded-lg bg-gray-100">
            <Image
              src={currentImage}
              alt={product.productName}
              layout="fill"
              objectFit="cover"
              unoptimized
            />
            {product.images.length > 1 && (
              <>
                <button
                  onClick={handlePreviousImage}
                  className="absolute left-4 top-1/2 flex h-12 w-12 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 opacity-0 transition-opacity hover:bg-white group-hover:opacity-100"
                  aria-label="Previous image"
                >
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                  >
                    <path
                      d="M15 18L9 12L15 6"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                    />
                  </svg>
                </button>
                <button
                  onClick={handleNextImage}
                  className="absolute right-4 top-1/2 flex h-12 w-12 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 opacity-0 transition-opacity hover:bg-white group-hover:opacity-100"
                  aria-label="Next image"
                >
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                  >
                    <path
                      d="M9 6L15 12L9 18"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                    />
                  </svg>
                </button>
              </>
            )}
          </div>

          {product.images.length > 1 && (
            <div className="grid grid-cols-6 gap-2">
              {product.images.map((img, index) => (
                <button
                  key={index}
                  onClick={() => setCurrentImageIndex(index)}
                  className={`aspect-square overflow-hidden rounded border-2 transition-all ${
                    index === currentImageIndex
                      ? 'border-black'
                      : 'border-transparent hover:border-gray-300'
                  }`}
                >
                  <Image
                    src={img}
                    alt={`${product.productName} ${index + 1}`}
                    width={80}
                    height={80}
                    objectFit="cover"
                    unoptimized
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="flex flex-col gap-6">
          <div className="border-b pb-4">
            <h1 className="mb-2 text-3xl font-semibold">
              {product.productName}
            </h1>
            <p className="text-gray-600">{product.categoryName}</p>
          </div>

          <div className="text-2xl font-medium">
            {product.promotionPrice ? (
              <>
                <span className="mr-2 text-red-600">
                  {product.promotionPrice.toLocaleString('vi-VN')}₫
                </span>
                <span className="text-lg text-gray-500 line-through">
                  {product.price.toLocaleString('vi-VN')}₫
                </span>
              </>
            ) : (
              <span>{product.displayPrice.toLocaleString('vi-VN')}₫</span>
            )}
          </div>

          <div>
            <div className="mb-3 flex items-center justify-between">
              <span className="font-medium">Select Color</span>
              <span className="text-gray-600">
                {product.selectedColorName}
              </span>
            </div>
            <div className="flex flex-wrap gap-2">
              {product.availableColors.map((color) => (
                <button
                  key={color.styleColor}
                  onClick={() => handleColorChange(color.styleColor)}
                  disabled={!color.isAvailable}
                  className={`h-20 w-20 overflow-hidden rounded border-2 transition-all ${
                    color.styleColor === product.selectedStyleColor
                      ? 'border-black shadow-md'
                      : 'border-gray-300 hover:border-gray-500'
                  } ${!color.isAvailable ? 'cursor-not-allowed opacity-50' : ''}`}
                  title={color.colorName}
                >
                  <Image
                    src={color.thumbnailImage}
                    alt={color.colorName}
                    width={80}
                    height={80}
                    objectFit="cover"
                    unoptimized
                  />
                </button>
              ))}
            </div>
          </div>

          <div>
            <div className="mb-3 font-medium">Select Size</div>
            <div className="grid grid-cols-3 gap-2">
              {product.availableSizes.map((size) => (
                <button
                  key={size.variantId}
                  onClick={() => setSelectedSize(size.variantId)}
                  disabled={!size.isAvailable}
                  className={`rounded border py-3 text-center text-sm transition-all ${
                    size.variantId === selectedSize
                      ? 'border-black bg-black text-white'
                      : size.isAvailable
                        ? 'border-gray-300 hover:border-black'
                        : 'cursor-not-allowed border-gray-200 text-gray-400 line-through'
                  }`}
                >
                  EU {size.sizeValue}
                </button>
              ))}
            </div>
          </div>

          <button
            onClick={handleAddToCart}
            disabled={!selectedSize}
            className="w-full rounded-full bg-black px-6 py-4 font-medium text-white transition-all hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-500"
          >
            Add to Cart
          </button>

          {product.description && (
            <div className="border-t pt-6">
              <h3 className="mb-3 text-xl font-medium">Product Description</h3>
              <p className="leading-relaxed text-gray-700">
                {product.description}
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
