package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.service.accommodation.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Operation(summary = "Get all available accommodations",
            description = "Get all available accommodations")
    @GetMapping
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationService.getAll(pageable);
    }

    @Operation(summary = "Get some accommodation by id",
            description = "Get some accommodation by id")
    @GetMapping("/{id}")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    @Operation(summary = "Create a new accommodation", description = "Create a new accommodation")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccommodationDto save(@RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.create(requestDto);
    }

    @Operation(summary = "Delete an accommodation by id",
            description = "Delete an accommodation by id")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        accommodationService.getById(id);
    }

    @Operation(summary = "Update an accommodation by id",
            description = "Update an accommodation by id")
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}")
    public AccommodationDto update(@PathVariable Long id,
                                   @RequestBody @Valid AccommodationUpdateDto updateDto) {
        return accommodationService.update(id, updateDto);
    }
}
