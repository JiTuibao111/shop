<template>
	<view class="classify">
		<view class="navBar">
			<view class="right" @click="toSearch">
				<input type="text" value="" placeholder="输入关键字" />
				<icon type="search" size="34rpx" color="#1296db"></icon>
			</view>
		</view>
		<view class="list" ref="list" :style="{height:listHight+'rpx'}">
			<scroll-view scroll-y="true" class="categoryList">
				<view class="categoryItem" :class="chooseCategory==item?'chooseStyle':''"
					v-for="(item,index) in category" :key="index" @click="toCategory(item)">
					{{item}}
				</view>
			</scroll-view>
			<scroll-view scroll-y="true" class="goodsList">
				<view class="goodsItem" v-for="(item,index) in goods" :key="index" @click="toDetail(item.goodsId)">
					<image class="image" :src="item.image" mode="widthFix">
					</image>
					<view class="goodsName">{{item.goodsName}}</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>
<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				category: [],
				goods: [],
				chooseCategory: '',
				listHight: 0,
			}
		},
		onLoad() {
			this.$api = api;
			this.getCategory();
		},
		methods: {
			// 获取商品分类
			async getCategory() {
				try {
					const res = await this.$api.get('/goods/findCategoryList', {}, {
						loading: true,
						showError: true
					});
					
					if (res && res.data) {
						this.category = res.data;
						if (this.category.length > 0) {
							this.getGoods(this.category[0]);
						}
						this.getListHeight();
					}
				} catch (error) {
					console.error('获取分类失败:', error);
				}
			},
			// 获取分类下的商品
			async getGoods(category) {
				if (!category) return;
				
				this.chooseCategory = category;
				try {
					const res = await this.$api.get('/goods/findByCategory', {
						category: category
					}, {
						loading: false,
						showError: true
					});
					
					if (res && res.data) {
						this.goods = res.data;
						if (!this.goods || this.goods.length === 0) {
							uni.showToast({
								icon: "none",
								title: "该分类暂无商品"
							});
						}
					}
				} catch (error) {
					console.error('获取商品失败:', error);
				}
			},
			//前往详情页
			toDetail(id) {
				uni.navigateTo({
					url: '/pages/detail/detail?id=' + id,
				});
			},
			//前往类别
			toCategory(item) {
				this.getGoods(item);
			},
			toSearch() {
				uni.navigateTo({
					url: '/pages/search/search',
				});
			},
			// 获取窗口界面高度
			getListHeight() {
				const systemInfo = uni.getSystemInfoSync();
				const wHeight = systemInfo.windowHeight;
				const wBottom = systemInfo.windowBottom || 0;
				
				this.listHight = (wHeight - wBottom) * 2;
				// #ifdef MP-WEIXIN
				this.listHight = (wHeight - 75) * 2;
				// #endif
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
		padding-top: 100rpx;

		/* #endif */
		.right {
			flex: 1;
			position: relative;
			width: 200rpx;
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

	.list {
		display: flex;

		.categoryList {
			flex: 1;

			.chooseStyle {
				color: #1296db;
				border-left: 6rpx solid #1296db;
			}

			.categoryItem {
				padding: 30rpx 0 30rpx 30rpx;
			}
		}

		.goodsList {
			flex: 3;

			.goodsItem {
				display: inline-block;
				margin: 15rpx;
				width: 250rpx;
				height: 380rpx;
				background-color: white;
				border-radius: 20rpx;
				text-align: center;
				overflow: hidden;
			}

			.image {
				width: 100%;
			}

			.goodsName {
				display: -webkit-box;
				-webkit-line-clamp: 2;
				-webkit-box-orient: vertical;
				overflow: hidden;
			}
		}
	}
</style>