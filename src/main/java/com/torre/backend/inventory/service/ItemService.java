package com.torre.backend.inventory.service;

import com.torre.backend.authorization.enums.StatusEnum;
import com.torre.backend.common.dtos.QueryParamsDto;
import com.torre.backend.common.exceptions.BaseException;
import com.torre.backend.inventory.dto.CreateItemDto;
import com.torre.backend.inventory.dto.ItemDto;
import com.torre.backend.inventory.entities.Category;
import com.torre.backend.inventory.entities.Item;
import com.torre.backend.inventory.mappers.ItemMapper;
import com.torre.backend.inventory.repository.CategoryRepository;
import com.torre.backend.inventory.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import static com.torre.backend.common.utils.PageableMapper.buildPage;
import static org.yaml.snakeyaml.nodes.Tag.STR;

@Service
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ItemDto> getItems(QueryParamsDto queryParamsDto) {
        String filter = queryParamsDto.getFilter();
        filter = "%" + filter + "%";
        Page<Item> pageItem = itemRepository.filterItems(filter, buildPage(queryParamsDto));
        log.info("page {}", pageItem.getContent());
        return pageItem.map(ItemMapper::toDto);
    }

    public void createItem(CreateItemDto itemDto, String username) {
        if(itemDto == null) throw new BaseException(HttpStatus.BAD_REQUEST, "Errores de validación");
        Category category = categoryRepository.findById(itemDto.getCategoryId()).orElse(null);
        if(category == null) throw new BaseException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        Item item = ItemMapper.toEntity(itemDto, category);
        item.setCreatedBy(username);
        itemRepository.save(item);
    }

    public void updateItem(Long id, CreateItemDto itemDto, String username) {
        if(itemDto == null) throw new BaseException(HttpStatus.BAD_REQUEST, "Errores de validación");
        Category category = categoryRepository.findById(itemDto.getCategoryId()).orElse(null);
        if(category == null) throw new BaseException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        Item item = itemRepository.findById(id).orElse(null);
        if(item == null) throw new BaseException(HttpStatus.NOT_FOUND, "Item no encontrada");
        ItemMapper.updateEntity(item, itemDto, category);
        item.setUpdatedBy(username);
        itemRepository.save(item);
    }

    public void updateStatus(Long id, String username,String newStatus) {
        Item item = itemRepository.findById(id).orElse(null);
        if(item == null) throw new BaseException(HttpStatus.NOT_FOUND, "Item no encontrada");
        item.setStatus(StatusEnum.valueOf(newStatus));
        itemRepository.save(item);
    }
}
