import { FormikProps } from 'formik';

import InputComponent from '@/common/components/input/components/InputComponent';
import { VIETNAMESE_PROVINCES } from '@/common/lib/vietnameseProvinces';

interface AddressFormValues {
  fullName: string;
  phone: string;
  tenDuong: string;
  xaQuan: string;
  tinhThanh: string;
  isDefault: boolean;
}

interface AddressFormProps {
  formik: FormikProps<AddressFormValues>;
}

const AddressForm = ({ formik }: AddressFormProps) => {
  return (
    <div className="flex flex-col gap-4">
      <h2 className="text-2xl font-semibold">Thông tin giao hàng</h2>

      <InputComponent
        label="Họ và tên"
        placeholder="Nhập họ và tên người nhận..."
        name="fullName"
        handleChange={formik.handleChange}
        value={formik.values.fullName}
        errors={formik.errors}
        handleBlur={formik.handleBlur}
      />

      <InputComponent
        label="Số điện thoại"
        placeholder="Nhập số điện thoại..."
        name="phone"
        handleChange={formik.handleChange}
        value={formik.values.phone}
        errors={formik.errors}
        handleBlur={formik.handleBlur}
      />

      <InputComponent
        label="Tên đường / Tòa nhà"
        placeholder="Nhập địa chỉ đường..."
        name="tenDuong"
        handleChange={formik.handleChange}
        value={formik.values.tenDuong}
        errors={formik.errors}
        handleBlur={formik.handleBlur}
      />

      <InputComponent
        label="Xã / Quận"
        placeholder="Nhập xã / quận..."
        name="xaQuan"
        handleChange={formik.handleChange}
        value={formik.values.xaQuan}
        errors={formik.errors}
        handleBlur={formik.handleBlur}
      />

      <div className="flex flex-col gap-1">
        <label className="text-sm font-semibold">Tỉnh / Thành phố</label>
        <select
          name="tinhThanh"
          value={formik.values.tinhThanh}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          className="input"
        >
          <option value="">Chọn tỉnh / thành phố</option>
          {VIETNAMESE_PROVINCES.map((province) => (
            <option key={province} value={province}>
              {province}
            </option>
          ))}
        </select>
        {formik.errors.tinhThanh && formik.touched.tinhThanh && (
          <span className="text-sm text-red-500">{formik.errors.tinhThanh}</span>
        )}
      </div>

      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => formik.setFieldValue('isDefault', !formik.values.isDefault)}
          className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
            formik.values.isDefault
              ? 'bg-blue-500 text-white hover:bg-blue-600'
              : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          }`}
        >
          {formik.values.isDefault ? '✓ Địa chỉ mặc định' : 'Đặt làm địa chỉ mặc định'}
        </button>
      </div>
    </div>
  );
};

export default AddressForm;
