import { useState } from 'react';

import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';

import { InputComponentProps } from '../types/input.types';

interface InputPasswordComponentProps extends InputComponentProps {
  rightLink?: {
    href: string;
    text: string;
  };
}

const InputPasswordComponent = ({
  label,
  placeholder,
  name,
  handleChange,
  handleBlur,
  value,
  errors,
  className,
  rightLink,
}: InputPasswordComponentProps) => {
  const [shown, setShown] = useState(false);

  return (
    <label className="flex flex-col">
      <div className="flex items-center justify-between">
        <span className="text-lg font-semibold">{label}</span>
        {rightLink && (
          <a href={rightLink.href} className="text-sm underline">
            {rightLink.text}
          </a>
        )}
      </div>
      <div className="relative w-full">
        <input
          type={shown ? 'text' : 'password'}
          className={`input w-full ${className || ''}`}
          placeholder={placeholder}
          id={name}
          name={name}
          onChange={handleChange}
          value={value}
          onBlur={handleBlur}
        />
        <button
          className="btn-icon absolute right-0 h-full px-2"
          onClick={() => setShown((prev) => !prev)}
          type="button"
        >
          {shown ? <AiOutlineEyeInvisible /> : <AiOutlineEye />}
        </button>
      </div>
      <div className="h-4 text-xs italic text-red-500">{errors[name]}</div>
    </label>
  );
};

export default InputPasswordComponent;
