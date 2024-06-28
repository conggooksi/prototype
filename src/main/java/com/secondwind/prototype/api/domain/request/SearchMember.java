package com.secondwind.prototype.api.domain.request;

import com.secondwind.prototype.common.dto.PaginationDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
public class SearchMember extends PaginationDto {
  private String loginId;
  private String name;

  public SearchMember(Integer limit, Integer offset, String orderBy, Direction direction, String loginId, String name) {
    super(limit, offset, orderBy, direction);
    this.loginId = loginId;
    this.name = name;
  }
}
