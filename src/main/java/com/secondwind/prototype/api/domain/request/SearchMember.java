package com.secondwind.prototype.api.domain.request;

import com.secondwind.prototype.common.dto.PaginationDto;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@Getter
public class SearchMember extends PaginationDto {
  private final String loginId;
  private final String name;

  public SearchMember(Integer limit, Integer offset, String orderBy, Direction direction, String loginId, String name) {
    super(limit, offset, orderBy, direction);
    this.loginId = loginId;
    this.name = name;
  }
}
