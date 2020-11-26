package posmy.interview.boot.dao.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import posmy.interview.boot.constant.BookStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookExample {

    private Long id;

    private String name;

    private BookStatus status;
}
