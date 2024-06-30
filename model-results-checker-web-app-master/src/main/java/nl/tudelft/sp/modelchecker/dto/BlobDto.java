package nl.tudelft.sp.modelchecker.dto;

import java.io.Serializable;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlobDto implements Serializable {
    URI sasLink;
}
