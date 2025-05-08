package org.example.expert.domain.todo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodoSearchResponse {

    private String title;
    private Long managerCount;
    private Long commentCount;

    public void setTitle(String title) {
        this.title = title;
    }
    public void setManagerCount(Long managerCount) {
        this.managerCount = managerCount;
    }
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

}
