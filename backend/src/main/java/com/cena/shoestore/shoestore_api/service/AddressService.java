package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.CreateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.response.AddressResponse;
import com.cena.shoestore.shoestore_api.entity.Address;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.AddressRepository;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {

    AddressRepository addressRepository;
    UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public List<AddressResponse> getAllAddresses() {
        User currentUser = getCurrentUser();
        log.info("Fetching all addresses for user: {}", currentUser.getEmail());

        List<Address> addresses = addressRepository.findByUser(currentUser);
        return addresses.stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse createAddress(CreateAddressRequest request) {
        User currentUser = getCurrentUser();
        log.info("Creating new address for user: {}", currentUser.getEmail());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetCurrentDefaultAddress(currentUser);
        }

        Address address = Address.builder()
                .user(currentUser)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .tenDuong(request.getTenDuong())
                .xaQuan(request.getXaQuan())
                .tinhThanh(request.getTinhThanh())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0)
                .build();

        address = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", address.getAddressId());

        return mapToAddressResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, UpdateAddressRequest request) {
        User currentUser = getCurrentUser();
        log.info("Updating address ID: {} for user: {}", addressId, currentUser.getEmail());

        Address address = addressRepository.findByAddressIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (Boolean.TRUE.equals(request.getIsDefault()) && address.getIsDefault() != 1) {
            unsetCurrentDefaultAddress(currentUser);
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setTenDuong(request.getTenDuong());
        address.setXaQuan(request.getXaQuan());
        address.setTinhThanh(request.getTinhThanh());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);

        address = addressRepository.save(address);
        log.info("Address updated successfully: {}", addressId);

        return mapToAddressResponse(address);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        User currentUser = getCurrentUser();
        log.info("Deleting address ID: {} for user: {}", addressId, currentUser.getEmail());

        Address address = addressRepository.findByAddressIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        addressRepository.delete(address);
        log.info("Address deleted successfully: {}", addressId);
    }

    @Transactional
    public AddressResponse setDefaultAddress(Long addressId) {
        User currentUser = getCurrentUser();
        log.info("Setting address ID: {} as default for user: {}", addressId, currentUser.getEmail());

        Address address = addressRepository.findByAddressIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        unsetCurrentDefaultAddress(currentUser);

        address.setIsDefault(1);
        address = addressRepository.save(address);

        log.info("Address set as default successfully: {}", addressId);
        return mapToAddressResponse(address);
    }

    private void unsetCurrentDefaultAddress(User user) {
        addressRepository.findByUserAndIsDefault(user, 1)
                .ifPresent(existingDefault -> {
                    existingDefault.setIsDefault(0);
                    addressRepository.save(existingDefault);
                    log.info("Unset previous default address ID: {}", existingDefault.getAddressId());
                });
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .tenDuong(address.getTenDuong())
                .xaQuan(address.getXaQuan())
                .tinhThanh(address.getTinhThanh())
                .isDefault(address.getIsDefault() == 1)
                .build();
    }
}
