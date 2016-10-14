package com.yoelglus.presentation.patterns.rmvp;


import com.yoelglus.presentation.patterns.data.ItemsRepository;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

public class RmvpAddItemPresenter extends AbstractPresenter<RmvpAddItemPresenter.View> {

    private ItemsRepository mItemsRepository;
    private Scheduler mIoScheduler;
    private Scheduler mMainScheduler;
    private String mContentText;
    private String mDetailText;
    private SubscriptionList mSubscriptionList = new SubscriptionList();

    public RmvpAddItemPresenter(ItemsRepository itemsRepository, Scheduler ioScheduler, Scheduler mainScheduler) {
        mItemsRepository = itemsRepository;
        mIoScheduler = ioScheduler;
        mMainScheduler = mainScheduler;
    }

    @Override
    public void onTakeView() {
        mSubscriptionList.add(mView.contentTextChanged()
                .doOnNext(contentText -> mContentText = contentText)
                .zipWith(mView.detailTextChanged().doOnNext(detailText -> mDetailText = detailText),
                        (content, detail) -> content.length() > 0 && detail.length() > 0)
                .subscribe(mView.setAddButtonEnabled()));

        mSubscriptionList.add(mView.addButtonClicks()
                .flatMap(aVoid -> mItemsRepository.addItem(mContentText, mDetailText)
                        .subscribeOn(mIoScheduler)
                        .observeOn(mMainScheduler)
                        .map(s -> (Void) null))
                .subscribe(mView.dismissView()));

        mSubscriptionList.add(mView.cancelButtonClicks().subscribe(mView.dismissView()));
    }

    @Override
    public void onDropView() {
        mSubscriptionList.unsubscribe();
    }

    public interface View {
        Observable<String> contentTextChanged();

        Observable<String> detailTextChanged();

        Observable<Void> addButtonClicks();

        Observable<Void> cancelButtonClicks();

        Action1<Boolean> setAddButtonEnabled();

        Action1<Void> dismissView();
    }
}
