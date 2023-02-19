package xyz.hugme.hugmebackend.list.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.hugme.hugmebackend.list.domain.List;
import xyz.hugme.hugmebackend.list.domain.repository.ListRepository;
import xyz.hugme.hugmebackend.list.dto.ListDTO;


@Service
@RequiredArgsConstructor
public class ListService {
    private final ListRepository listRepository;

    public Slice<ListDTO> findAll(Pageable pageable){
        Slice<List> slice = listRepository.findAllBy(pageable);

        return slice.map(List::toDTO);
    }
}
