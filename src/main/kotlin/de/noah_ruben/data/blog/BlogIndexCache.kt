package de.noah_ruben.data.blog

class BlogIndexCache {
    @Volatile
    private var visiblePosts: List<BlogPostRecord> = emptyList()

    fun refresh(posts: List<BlogPostRecord>) {
        visiblePosts = posts.sortedByDescending(BlogPostRecord::publishedDate)
    }

    fun visiblePosts(): List<BlogPostRecord> = visiblePosts
}
