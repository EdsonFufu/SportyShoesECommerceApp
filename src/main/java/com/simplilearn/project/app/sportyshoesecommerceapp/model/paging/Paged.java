package com.simplilearn.project.app.sportyshoesecommerceapp.model.paging;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Paged<T> {

    private Page<T> page;

    private Paging paging;

}