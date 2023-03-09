package com.github.howieyoung91.commentwriter.generate.java

import com.github.howieyoung91.commentwriter.generate.CommentGenerator
import com.github.howieyoung91.commentwriter.generate.Query

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : CommentGenerator {
    override fun generate(query: Query): String {
        return query.text
    }
}
