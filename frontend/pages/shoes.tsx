import { useState, useEffect } from 'react';

import { useRouter } from 'next/router';

import { productService } from '@/common/api/services/productService';
import Spinner from '@/common/components/spinner/components/Spinner';
import { mapBackendProductsToSimple } from '@/common/lib/mapBackendProduct';
import ProductList from '@/modules/products/components/ProductList';

export interface ShoesPageProps {
  products: SimpleProduct[];
  blurDataUrls: { [key: string]: string };
}

const ShoesPage = () => {
  const router = useRouter();
  const { gender } = router.query;

  const [products, setProducts] = useState<SimpleProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!router.isReady) {
      return;
    }

    setProducts([]);
    setLoading(true);

    let isCancelled = false;

    const fetchProducts = async () => {
      try {
        setLoading(true);
        setError(null);

        const backendProducts = await productService.getProducts({
          pageSize: 200,
        });

        if (isCancelled) {
          return;
        }

        let filteredProducts = backendProducts;
        if (gender) {
          filteredProducts = backendProducts.filter(
            (product) => product.categoryName?.toLowerCase() === (gender as string).toLowerCase()
          );
        }

        const mappedProducts = mapBackendProductsToSimple(filteredProducts);
        setProducts(mappedProducts);
      } catch (err) {
        if (isCancelled) return;
        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load products. Please try again later.'
        );
      } finally {
        if (!isCancelled) {
          setLoading(false);
        }
      }
    };

    fetchProducts();

    return () => {
      isCancelled = true;
    };
  }, [router.isReady, gender]);

  if (loading) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <div className="text-center">
          <h2 className="mb-4 text-2xl font-bold text-red-500">Error</h2>
          <p className="text-gray-600">{error}</p>
          <button onClick={() => router.reload()} className="btn mt-4">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return <ProductList products={products} blurDataUrls={{}} />;
};

export default ShoesPage;
