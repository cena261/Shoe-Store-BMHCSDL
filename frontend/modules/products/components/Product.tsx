import { useState } from 'react';

import { motion } from 'framer-motion';
import Image from 'next/image';
import Link from 'next/link';
import { useTimeoutFn } from 'react-use';

import { defaultEase } from '@/common/animations/easings';
interface Props extends SimpleProduct {
  blurDataUrl?: string;
}

const ProductComponent = ({
  id,
  attributes: {
    name,
    price,
    promotionPrice,
    category,
    colorVariants,
    images: {
      data: [image],
    },
    slug,
  },
  blurDataUrl,
}: Props) => {
  const [active, setActive] = useState(false);
  const [overflow, setOverflow] = useState(false);
  const [currentImage, setCurrentImage] = useState<string>('');

  useTimeoutFn(() => {
    setOverflow(true);
  }, 200);

  const mainImageUrl = image.attributes.hash.startsWith('http')
    ? image.attributes.hash
    : `${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'}/images/${image.attributes.hash}`;

  const displayImage = currentImage || mainImageUrl;

  return (
    <motion.div className="h-max w-max" layoutId={id}>
      <Link href={`/${slug}`} passHref>
        <a
          className={`block w-72 cursor-pointer ${
            overflow && 'overflow-hidden'
          }  2xl:w-96`}
          onFocus={() => setActive(true)}
          onBlur={() => setActive(false)}
          onMouseEnter={() => setActive(true)}
          onMouseLeave={() => setActive(false)}
        >
          <motion.div
            transition={{
              duration: 0.3,
              ease: defaultEase,
            }}
            animate={{ scale: active ? 1.07 : 1 }}
          >
            <div className="relative h-96 w-72 rounded-lg bg-gray-100 2xl:h-128 2xl:w-96">
              <Image
                layout="fill"
                objectFit="cover"
                src={displayImage}
                alt={name}
                quality={75}
                className="h-full w-full rounded-lg"
                unoptimized
              />
            </div>
          </motion.div>
        </a>
      </Link>

      {colorVariants && colorVariants.length > 1 && (
        <div className="mt-2 flex flex-wrap gap-2 px-2">
          {colorVariants
            .filter((variant) => variant.mainImageUrl)
            .map((variant, index) => {
              const variantImageUrl = variant.mainImageUrl.startsWith('http')
                ? variant.mainImageUrl
                : `${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'}/images/${variant.mainImageUrl}`;

              return (
                <Link
                  key={`${variant.styleColor}-${index}`}
                  href={`/${slug}?styleColor=${variant.styleColor}`}
                  passHref
                >
                  <a
                    className="h-12 w-12 cursor-pointer overflow-hidden rounded border-2 border-transparent transition-all hover:border-black"
                    onMouseEnter={() => setCurrentImage(variantImageUrl)}
                    onMouseLeave={() => setCurrentImage('')}
                    title={variant.colorName}
                  >
                    <Image
                      src={variantImageUrl}
                      alt={variant.colorName}
                      width={48}
                      height={48}
                      objectFit="cover"
                      unoptimized
                    />
                  </a>
                </Link>
              );
            })}
        </div>
      )}

      <div className="mt-2 px-2">
        <div className="mb-2">
          <h4 className="text-base font-semibold">{name}</h4>
          <h5 className="text-sm text-zinc-500">
            {category[0].toUpperCase() + category.slice(1)}
          </h5>
        </div>
        <div className="text-base font-semibold">
          {(promotionPrice || price).toLocaleString('vi-VN')}₫
          {promotionPrice && (
            <span className="ml-2 text-sm text-zinc-500 line-through">
              {price.toLocaleString('vi-VN')}₫
            </span>
          )}
        </div>
      </div>
    </motion.div>
  );
};

export default ProductComponent;
