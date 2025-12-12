<template>
	<view>

		<view class="navBar">
			<view class="left">海洋生物用户端</view>
			<view class="right" @click="toSearch">
				<input type="text" value="" placeholder="输入关键词" />
				<icon type="search" size="34rpx" color="#1296db"></icon>
			</view>
		</view>
		<!-- 排序选择 -->
		<view class="sort-bar">
			<view class="sort-item" :class="{ active: sortField === '' }" @click="changeSort('', '')">
				默认
			</view>
			<view class="sort-item" :class="{ active: sortField === 'price' && sortOrder === 'asc' }" @click="changeSort('price', 'asc')">
				价格↑
			</view>
			<view class="sort-item" :class="{ active: sortField === 'price' && sortOrder === 'desc' }" @click="changeSort('price', 'desc')">
				价格↓
			</view>
			<view class="sort-item" :class="{ active: sortField === 'sales' && sortOrder === 'desc' }" @click="changeSort('sales', 'desc')">
				销量↓
			</view>
		</view>
		<!-- 3.展示数据到页面上 -->
		<swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000" class="swiper">
			<swiper-item v-for="(item,index) in banners" :key="index">
				<image :src="item.imgurl" mode="widthFix" class="image"></image>
			</swiper-item>
		</swiper>
		<!-- 点击商品跳转到商品的详情页   @click="toDetail(item.goodsId)" -->
		<view class="goodsList">
			<view class="item" v-for="item in goodsList" :key="item.goodsId" @click="toDetail(item.goodsId)">
				<image :src="item.image" mode="widthFix"></image>
				<view class="goodsName-price">
					<view class="goodsName">{{ item.goodsName }}</view>
					<view class="price">
						<text class="yuan">¥</text>
						<text>{{ item.price.toFixed(2).split('.')[0] }}.</text>
						<text class="decimal">{{ item.price.toFixed(2).split('.')[1] }}</text>
						<text class="sales">已售{{ item.sales }}件</text>
					</view>
				</view>
			</view>
		</view>
		<uni-load-more :status="status" :icon-size="16" :content-text="contentText"></uni-load-more>
		<!-- 		<view class="pagination">
			<text @click="prevPage" :class="{ 'disabled': currentPage === 1 }">上一页</text>
			<text>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</text>
			<text @click="nextPage" :class="{ 'disabled': currentPage === totalPages }">下一页</text>
		</view> -->

	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				// 2.定义一个变量 保存后端返回的数据
				banners: [],

				//pageNum/currentPage第几页 默认第一页  每页展示6件商品信息 总页数默认值为1  看goods表商品的总数量/每页大小
				// 前台传递指定的数据到后台
				goodsList: [],
				currentPage: 1,
				totalPages: 1,
				pageSize: 6,
				sortField: '',
				sortOrder: '',

				status: "no-more",
				contentText: {
					contentdown: "上拉加载更多",
					contentrefresh: "正在加载",
					contentnomore: "没有更多加载"
				}
			}
		},
		onLoad() {
			// 将api挂载到this上
			this.$api = api;
			this.getBanners();
		},
		onReady() {
			this.getPageByGoods();
		},
		// 下拉刷新
		onPullDownRefresh() {
			this.goodsList = [];
			this.currentPage = 1;
			this.status = 'more';
			this.getBanners();
			this.getPageByGoods();
		},
		
		// 改变排序
		changeSort(field, order) {
			this.sortField = field;
			this.sortOrder = order;
			this.goodsList = [];
			this.currentPage = 1;
			this.status = 'more';
			this.getPageByGoods();
		},
		// 上拉加载更多
		onReachBottom() {
			if (this.currentPage >= this.totalPages) {
				this.status = 'no-more';
				uni.showToast({
					icon: 'none',
					title: '没有更多数据了'
				});
			} else {
				this.currentPage++;
				this.getPageByGoods();
			}
		},
		methods: {
			// 获取轮播图数据
			async getBanners() {
				try {
					const res = await this.$api.get('/banner/findBanner', {}, {
						loading: false,
						showError: false
					});
					if (res && res.data) {
						this.banners = res.data;
					}
				} catch (error) {
					console.error('获取轮播图失败:', error);
				}
			},
			// 获取商品分页数据
			async getPageByGoods() {
				this.status = 'loading';
				try {
					const params = {
						currentPage: this.currentPage,
						pageSize: this.pageSize
					};
					if (this.sortField) {
						params.sortField = this.sortField;
						params.sortOrder = this.sortOrder;
					}
					const res = await this.$api.get('/goods/getPageByGoods', params, {
						loading: false,
						showError: true
					});

					// 停止下拉刷新
					uni.stopPullDownRefresh();

					if (res && res.data) {
						const pageData = res.data;
						// 追加数据到列表
						if (pageData.records && pageData.records.length > 0) {
							this.goodsList.push(...pageData.records);
						}
						
						// 更新分页信息
						this.currentPage = pageData.current;
						this.totalPages = pageData.pages;
						
						// 更新加载状态
						if (this.currentPage >= this.totalPages) {
							this.status = 'no-more';
						} else {
							this.status = 'more';
						}
					}
				} catch (error) {
					console.error('获取商品列表失败:', error);
					this.status = 'no-more';
					uni.stopPullDownRefresh();
				}
			},
			// 跳转到商品查询页面
			toSearch() {
				uni.navigateTo({
					url: '/pages/search/search',
					success: res => {},
					fail: () => {},
					complete: () => {}
				});
			},
			// 跳转到商品详情页面
			toDetail(id) {
				uni.navigateTo({
					url: '/pages/detail/detail?id=' + id,
				});
			},

		}
	}
</script>

<style lang="scss">
	.navBar {
		height: 500rpx;
		background-color: white;
		padding: 0 30rpx;
		padding-top: 20rpx;
		/* #ifdef MP-WEIXIN */
		padding-top: 100rpx;
		/* #endif */
		overflow: hidden;
		background: linear-gradient(to bottom, #1296db, #f7f7f7);

		.left {
			flex: 1;
			color: white;
			font-size: 40rpx;
			font-weight: 700;
		}

		.right {
			position: relative;
			margin-top: 20rpx;
			flex: 2;
		}

		input {
			padding: 10rpx 0rpx;
			padding-left: 60rpx;
			border: 1rpx solid #ccc;
			border-radius: 50rpx;
			font-size: 30rpx;
			background-color: white;
		}

		icon {
			position: absolute;
			left: 20rpx;
			top: 16rpx;
		}
	}

	.swiper {
		height: 550rpx;
		margin: 0 20rpx;
		margin-top: -320rpx;
		border-radius: 50rpx;
		overflow: hidden;

		image {
			width: 100%;
		}
	}

	.goodsList {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex-wrap: wrap;
		margin: 0 20rpx;
		margin-bottom: 20rpx;

		.item {
			box-sizing: border-box;
			margin-top: 25rpx;
			width: 345rpx;
			height: 500rpx;
			border-radius: 20rpx;
			background-color: white;
			overflow: hidden;

		}

		image {
			width: 100%;
			border-radius: 20rpx 20rpx 0 0;
		}

		.goodsName-price {
			padding: 15rpx 20rpx;

			.goodsName {
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
			}

			.price {
				display: flex;
				align-items: baseline;
				padding: 20rpx 0;
				font-size: 38rpx;
				font-weight: 700;
				color: red;
			}

			.yuan,
			.decimal {
				font-size: 32rpx;
			}

			.sales {
				color: #646566;
				margin-left: 10rpx;
				font-weight: normal;
				font-size: 28rpx;
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
			}
		}
	}
	
	.sort-bar {
		display: flex;
		background-color: #fff;
		padding: 20rpx;
		margin: 20rpx;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
		
		.sort-item {
			flex: 1;
			text-align: center;
			padding: 10rpx 0;
			font-size: 28rpx;
			color: #666;
			border-right: 1rpx solid #eee;
			
			&:last-child {
				border-right: none;
			}
			
			&.active {
				color: #1296db;
				font-weight: bold;
			}
		}
	}
</style>