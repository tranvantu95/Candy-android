package com.candy.android.custom.callbacks;

import com.candy.android.model.PerformerFootprint;

/**
 * Created by namhv on 10/18/16.
 * Handle click on item Favorite Performer
 */

public interface OnFavoriteFootstepsClickListener {
    void unFavorite(PerformerFootprint performer);

    void addFavorite(PerformerFootprint performer);
}
