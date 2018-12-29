package com.candy.android.custom.callbacks;

import com.candy.android.model.PerformerFavorite;

/**
 * Created by namhv on 10/18/16.
 * Handle click on item Favorite Performer
 */

public interface OnFavoritePerformerClickListener {
    void onChat(PerformerFavorite performer);

    void onRemove(PerformerFavorite performer);

    void onOpenDetail(PerformerFavorite performer);
}
