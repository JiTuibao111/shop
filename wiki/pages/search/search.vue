<template>
	<view>
		<view class="navBar">
			<view class="center">
				<!-- 输入框绑定搜索关键字，确认搜索 -->
				<input type="text" v-model="searchKey" placeholder="输入关键字" @confirm="search" />
				<!-- 搜索图标 -->
				<icon type="search" size="34rpx" color="#1296db"></icon>
			</view>
			<!-- 搜索按钮 -->
			<view class="right" @click="search">搜索</view>
		</view>
		<view class="colorBg"></view>
		<!-- 点击商品跳转到商品的详情页 @click="toDetail(item.goodsId)" -->
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
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				goodsList: [],
				currentPage: 1,
				pageSize: 6,
				totalPages: 1,
				searchKey: "",
				status: "no-more",
				contentText: {
					contentdown: "上拉加载更多",
					contentrefresh: "正在加载",
					contentnomore: "没有更多数据"
				}
			}
		},
		onLoad() {
			this.$api = api;
		},
		onReady() {
			// 初始加载所有商品
			this.getPageQueryByGoods();
		},
		// 上拉加载更多
		onReachBottom() {
			if (this.currentPage >= this.totalPages) {
				this.status = "no-more";
				uni.showToast({
					icon: "none",
					title: '没有更多数据了'
				});
			} else {
				this.currentPage++;
				this.getPageQueryByGoods();
			}
		},
		methods: {
			// 跳转到商品详情页面
			toDetail(id) {
				uni.navigateTo({
					url: '/pages/detail/detail?id=' + id
				});
			},
			// 获取分页商品数据
			async getPageQueryByGoods() {
				this.status = 'loading';
				try {
					const res = await this.$api.get('/goods/getPageQueryByGoods', {
						searchKey: this.searchKey || '',
						currentPage: this.currentPage,
						pageSize: this.pageSize
					}, {
						loading: false,
						showError: true
					});

					if (res && res.data) {
						const pageData = res.data;
						
						// 分页数据处理：第一页覆盖，后续页追加
						if (this.currentPage === 1) {
							this.goodsList = pageData.records || [];
						} else {
							this.goodsList = [...this.goodsList, ...(pageData.records || [])];
						}

						// 更新分页信息
						this.currentPage = pageData.current;
						this.totalPages = pageData.pages;

						// 更新加载状态
						this.status = this.currentPage >= this.totalPages ? "no-more" : "more";
					}
				} catch (error) {
					console.error('获取商品列表失败:', error);
					this.status = "no-more";
				}
			},
			// 搜索
			search() {
				if (!this.searchKey.trim()) {
					uni.showToast({
						icon: "none",
						title: '请输入搜索关键词'
					});
					return;
				}
				
				// 搜索时重置数据
				this.goodsList = [];
				this.currentPage = 1;
				this.totalPages = 1;
				this.status = 'more';
				this.getPageQueryByGoods();
			}
		}
	}
</script>

<style lang="scss">
	.navBar {
		display: flex;
		height: 100rpx;
		background-color: white;
		align-items: center;
		padding: 0 30rpx;
		/* #ifdef MP-WEIXIN */
		padding-top: 150rpx;
		/* #endif */

		.left {
			flex: 1;
		}

		.center {
			position: relative;
			flex: 5;
		}

		.right {
			flex: 1;
			padding-left: 20rpx;
			text-align: center;
		}

		input {
			padding: 10rpx 0rpx;
			padding-left: 60rpx;
			border: 1rpx solid #ccc;
			border-radius: 50rpx;
			font-size: 30rpx;
		}

		icon {
			position: absolute;
			left: 20rpx;
			top: 16rpx;
		}
	}

	.colorBg {
		display: block;
		height: 300rpx;
		margin-bottom: -300rpx;
		border-radius: 0 0 50rpx 50rpx;
		background: linear-gradient(to bottom, #1296db, #f7f7f7);
	}

	.goodsList {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex-wrap: wrap;
		margin: 0 20rpx;

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
			}
		}
	}

	.btm {
		padding: 35rpx;
		text-align: center;
		color: #ccc;
	}
</style>