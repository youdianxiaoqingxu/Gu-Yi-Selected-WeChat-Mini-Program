package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.common.UserContext;
import com.guyi.dto.AddressDTO;
import com.guyi.service.AddressService;
import com.guyi.vo.AddressVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<AddressVO>> list() {
        return Result.ok(addressService.list(UserContext.get()));
    }

    @PostMapping
    public Result<AddressVO> add(@Valid @RequestBody AddressDTO dto) {
        return Result.ok(addressService.add(UserContext.get(), dto));
    }

    @PutMapping("/{id}")
    public Result<AddressVO> update(@PathVariable Integer id, @Valid @RequestBody AddressDTO dto) {
        return Result.ok(addressService.update(UserContext.get(), id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        addressService.remove(UserContext.get(), id);
        return Result.ok();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Integer id) {
        addressService.setDefault(UserContext.get(), id);
        return Result.ok();
    }
}
