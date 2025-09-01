package ganadinote.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
	private Integer tagCd;
    private String tagName; 
    private Integer upTag;
}
