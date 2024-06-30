package nl.tudelft.sp.modelchecker.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDto<E> {
    List<E> recordDtos;
}
