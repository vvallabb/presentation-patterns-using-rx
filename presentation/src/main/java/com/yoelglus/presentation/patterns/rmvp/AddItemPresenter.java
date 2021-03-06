package com.yoelglus.presentation.patterns.rmvp;


import com.yoelglus.presentation.patterns.data.ItemsRepository;
import com.yoelglus.presentation.patterns.navigator.Navigator;

import rx.Observable;

class AddItemPresenter extends AbstractPresenter<AddItemPresenter.View> {

    private ItemsRepository itemsRepository;
    private Navigator navigator;

    AddItemPresenter(ItemsRepository itemsRepository, Navigator navigator) {
        this.itemsRepository = itemsRepository;
        this.navigator = navigator;
    }

    private void addItem(ItemToAdd itemToAdd) {
        unsubscribeOnViewDropped(itemsRepository.addItem(itemToAdd.content, itemToAdd.details).subscribe(s -> navigator.closeCurrentScreen()));
    }

    @Override
    protected void onTakeView(AddItemPresenter.View view) {

        Observable<ItemToAdd> addEnabled = Observable.combineLatest(view.contentTextChanged(),
                view.detailTextChanged(),
                ItemToAdd::new).doOnNext(itemToAdd -> view.setAddButtonEnabled(itemToAdd.valid()));

        unsubscribeOnViewDropped(view.addButtonClicks()
                .withLatestFrom(addEnabled, (aVoid, itemToAdd) -> itemToAdd)
                .subscribe(this::addItem));

        unsubscribeOnViewDropped(view.cancelButtonClicks().subscribe(aVoid -> navigator.closeCurrentScreen()));
    }

    private static class ItemToAdd {

        private String content;
        private String details;

        private ItemToAdd(String content, String details) {
            this.content = content;
            this.details = details;
        }

        private boolean valid() {
            return content.length() > 0 && details.length() > 0;
        }
    }

    public interface View {

        void setAddButtonEnabled(boolean enabled);

        Observable<String> contentTextChanged();

        Observable<String> detailTextChanged();

        Observable<Void> addButtonClicks();

        Observable<Void> cancelButtonClicks();
    }
}
