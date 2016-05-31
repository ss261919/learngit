package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;

import static android.support.v7.widget.RecyclerView.ViewHolder;


/**
 * Created by Administrator on 2016/3/22.
 */
public class RedFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_FOOTER = 2;
    private final int width;
    private final int height;
    private final List<String> redAdsImgUrlList;
    Map<String, List<String>> dataMap;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private RedFragmentAdapter.onItemViewClickListener onItemViewClickListener;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public RedFragmentAdapter(Context context, Map<String, List<String>> dataMap, DisplayImageOptions options, ImageLoader imageLoader, int width, int height) {
        mContext = context;
        this.options = options;
        this.dataMap = dataMap;
        redAdsImgUrlList = dataMap.get("redAdsImgUrlList");
        this.imageLoader = imageLoader;
        mLayoutInflater = LayoutInflater.from(context);
        this.width = width;
        this.height = height;
    }

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(RedFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_1) {
            View view = mLayoutInflater.inflate(R.layout.red_item_type1, parent, false);
            RedViewHolder redViewHolder = new RedViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return redViewHolder;
        } else if (viewType == TYPE_2) {
            View view = mLayoutInflater.inflate(R.layout.red_item_type2, parent, false);
            AdsViewHolder adsViewHolder = new AdsViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return adsViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (holder instanceof RedViewHolder) {
            if (redAdsImgUrlList.size() <= dataMap.get("company_logo").size()) {
                if (position < (2 * redAdsImgUrlList.size())) {
                    if (position % 2 == 0) {
                        if (position == 0) {
                            setDataOne((RedViewHolder) holder, position);
                        } else {
                            setDataTwo((RedViewHolder) holder, position);
                        }
                    }
                } else {
                    String companyIconUrl = dataMap.get("company_logo").get(position - redAdsImgUrlList.size());
                    ((RedViewHolder) holder).redItemTvCompanyName.setText(dataMap.get("company_name").get(position - redAdsImgUrlList.size()));
                    ((RedViewHolder) holder).redItemTvCompanyWish.setText(dataMap.get("comment").get(position - redAdsImgUrlList.size()));
                    ((RedViewHolder) holder).redItemTvMoney.setText(dataMap.get("money").get(position - redAdsImgUrlList.size()) + "元");
                    ((RedViewHolder) holder).redItemCompanyImg.setTag(companyIconUrl);
                    ((RedViewHolder) holder).redAdsItemOpenRed.setImageResource(R.mipmap.open_red);
                    imageLoader.loadImage(companyIconUrl, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            ((RedViewHolder) holder).redItemCompanyImg.setImageResource(R.mipmap.red_head_icon);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (((RedViewHolder) holder).redItemCompanyImg.getTag().equals(imageUri)) {
                                ((RedViewHolder) holder).redItemCompanyImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 15, 15));
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    if (onItemViewClickListener != null) {
                        ((RedViewHolder) holder).redAdsItemOpenRed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                            }
                        });
                    }
                }
            } else {
                if (position < 2 * dataMap.get("company_logo").size()) {
                    if (position % 2 == 0) {
                        if (position == 0) {
                            setDataOne((RedViewHolder) holder, position);
                        } else {
                            setDataTwo((RedViewHolder) holder, position);
                        }
                    }
                }
            }
        } else if (holder instanceof AdsViewHolder) {
            if (redAdsImgUrlList.size() <= dataMap.get("company_logo").size()) {
                if (position < (2 * redAdsImgUrlList.size()) && !(position % 2 == 0)) {
                    String adsIconUrl = redAdsImgUrlList.get(position - ((position + 1) / 2));
                    ((AdsViewHolder) holder).adsItemImg.setTag(adsIconUrl);
                    imageLoader.loadImage(adsIconUrl, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (((AdsViewHolder) holder).adsItemImg.getTag().equals(imageUri)) {
                                ((AdsViewHolder) holder).adsItemImg.setImageBitmap(loadedImage);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    if (onItemViewClickListener != null) {
                        ((AdsViewHolder) holder).adsItemImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemViewClickListener.onItemClick(((AdsViewHolder) holder).adsItemImg, holder.getLayoutPosition());
                            }
                        });
                    }
                }
            } else {
                if (position < 2 * dataMap.get("company_logo").size()) {
                    if (!(position % 2 == 0)) {
                        String adsIconUrl = redAdsImgUrlList.get(position - ((position + 1) / 2));
                        ((AdsViewHolder) holder).adsItemImg.setTag(adsIconUrl);
                        imageLoader.loadImage(adsIconUrl, options, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                if (((AdsViewHolder) holder).adsItemImg.getTag().equals(imageUri)) {
                                    ((AdsViewHolder) holder).adsItemImg.setImageBitmap(loadedImage);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                        if (onItemViewClickListener != null) {
                            ((AdsViewHolder) holder).adsItemImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onItemViewClickListener.onItemClick(((AdsViewHolder) holder).adsItemImg, holder.getLayoutPosition());
                                }
                            });
                        }
                    }
                } else {
                    String adsIconUrl = redAdsImgUrlList.get(position - dataMap.get("company_name").size());
                    ((AdsViewHolder) holder).adsItemImg.setTag(adsIconUrl);
                    imageLoader.loadImage(adsIconUrl, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (((AdsViewHolder) holder).adsItemImg.getTag().equals(imageUri)) {
                                ((AdsViewHolder) holder).adsItemImg.setImageBitmap(loadedImage);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    if (onItemViewClickListener != null) {
                        ((AdsViewHolder) holder).adsItemImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemViewClickListener.onItemClick(((AdsViewHolder) holder).adsItemImg, holder.getLayoutPosition());
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (redAdsImgUrlList.size() <= dataMap.get("company_name").size()) {
            if (position < (2 * redAdsImgUrlList.size()) && !(position % 2 == 0)) {
                return TYPE_2;
            } else if (position < (2 * redAdsImgUrlList.size()) && position % 2 == 0) {
                return TYPE_1;
            } else {
                return TYPE_1;
            }
        } else {
            if (position < (2 * dataMap.get("company_name").size()) && !(position % 2 == 0)) {
                return TYPE_2;
            } else if (position < (2 * dataMap.get("company_name").size()) && position % 2 == 0) {
                return TYPE_1;
            } else {
                return TYPE_2;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (redAdsImgUrlList.size() <= dataMap.get("company_name").size())
            return dataMap == null ? 0 : (redAdsImgUrlList.size() + dataMap.get("company_name").size());
        else
            return dataMap == null ? 0 : (redAdsImgUrlList.size() + dataMap.get("company_name").size());
    }

    class RedViewHolder extends ViewHolder {

        private final RelativeLayout redAdsItemRL;
        private final ImageView redAdsItemOpenRed;
        ImageView redItemCompanyImg;
        TextView redItemTvCompanyName;
        TextView redItemTvCompanyWish;
        TextView redItemTvMoney;

        public RedViewHolder(View itemView) {
            super(itemView);
            redItemCompanyImg = (ImageView) itemView.findViewById(R.id.redAdsItemCompanyImg);
            redAdsItemOpenRed = (ImageView) itemView.findViewById(R.id.redAdsItemOpenRed);
            redItemTvCompanyName = (TextView) itemView.findViewById(R.id.redAdsItemTvCompanyName);
            redItemTvCompanyWish = (TextView) itemView.findViewById(R.id.redAdsItemTvCompanyWish);
            redItemTvMoney = (TextView) itemView.findViewById(R.id.redAdsItemTvMoney);
            redAdsItemRL = (RelativeLayout) itemView.findViewById(R.id.redAdsItemRL);
        }

    }

    private class AdsViewHolder extends ViewHolder {
        ImageView adsItemImg;

        public AdsViewHolder(View itemView) {
            super(itemView);
            adsItemImg = (ImageView) itemView.findViewById(R.id.adsItemImg);
        }
    }

    void setDataOne(final RedViewHolder holder, int position) {
        String companyIconUrl = dataMap.get("company_logo").get(position);
        holder.redItemTvCompanyName.setText(dataMap.get("company_name").get(position));
        holder.redItemTvCompanyWish.setText(dataMap.get("comment").get(position));
        holder.redItemTvMoney.setText(dataMap.get("money").get(position) + "元");
        holder.redItemCompanyImg.setTag(companyIconUrl);
        holder.redAdsItemOpenRed.setImageResource(R.mipmap.open_red);
        imageLoader.loadImage(companyIconUrl, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((RedViewHolder) holder).redItemCompanyImg.setImageResource(R.mipmap.red_head_icon);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (((RedViewHolder) holder).redItemCompanyImg.getTag().equals(imageUri)) {
                    ((RedViewHolder) holder).redItemCompanyImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 15, 15));
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        if (onItemViewClickListener != null) {
            holder.redAdsItemOpenRed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
    }

    void setDataTwo(final RedViewHolder holder, int position) {
        String companyIconUrl = dataMap.get("company_logo").get(position - ((position + 1) / 2));
        holder.redItemTvCompanyName.setText(dataMap.get("company_name").get(position - ((position + 1) / 2)));
        holder.redItemTvCompanyWish.setText(dataMap.get("comment").get(position - ((position + 1) / 2)));
        holder.redItemTvMoney.setText(dataMap.get("money").get(position - ((position + 1) / 2)) + "元");
        holder.redItemCompanyImg.setTag(companyIconUrl);
        holder.redAdsItemOpenRed.setImageResource(R.mipmap.open_red);
        imageLoader.loadImage(companyIconUrl, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((RedViewHolder) holder).redItemCompanyImg.setImageResource(R.mipmap.red_head_icon);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (((RedViewHolder) holder).redItemCompanyImg.getTag().equals(imageUri)) {
                    ((RedViewHolder) holder).redItemCompanyImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 15, 15));
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        if (onItemViewClickListener != null) {
            holder.redAdsItemOpenRed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
    }
}
