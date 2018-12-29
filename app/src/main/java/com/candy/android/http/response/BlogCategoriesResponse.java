package com.candy.android.http.response;

import com.candy.android.model.BlogCategory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlogCategoriesResponse {

    @SerializedName("blog")
    private Blog blog;

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public static class Blog {

        @SerializedName("categories")
        private List<BlogCategory> categories;

        public List<BlogCategory> getCategories() {
            return categories;
        }

        public void setCategories(List<BlogCategory> categories) {
            this.categories = categories;
        }

    }

}
