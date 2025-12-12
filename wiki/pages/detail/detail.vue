<template>
	<view class="container">
		<!-- 商品图片 -->
		<view class="goods-image">
			<image :src="goodsDetail.image || '/static/logo.png'" mode="aspectFit" class="image"></image>
		</view>
		
		<!-- 商品信息 -->
		<view class="goods-info">
			<view class="goods-name">{{ goodsDetail.goodsName || '商品名称' }}</view>
			<view class="goods-price">
				<text class="price-symbol">¥</text>
				<text class="price-value">{{ formatPrice(goodsDetail.price) }}</text>
			</view>
			<view class="goods-meta">
				<text class="meta-item">销量：{{ goodsDetail.sales || 0 }}件</text>
				<text class="meta-item">库存：{{ goodsDetail.stock || 0 }}件</text>
			</view>
		</view>
		
		<!-- 商品详情 -->
		<view class="goods-detail">
			<view class="detail-title">商品详情</view>
			<view class="detail-item">
				<text class="label">品牌：</text>
				<text class="value">{{ goodsDetail.brand || '暂无' }}</text>
			</view>
			<view class="detail-item">
				<text class="label">分类：</text>
				<text class="value">{{ goodsDetail.category || '暂无' }}</text>
			</view>
			<view class="detail-item">
				<text class="label">规格：</text>
				<text class="value">{{ goodsDetail.specifications || '暂无' }}</text>
			</view>
			<view class="detail-item">
				<text class="label">描述：</text>
				<text class="value">{{ goodsDetail.description || '暂无描述' }}</text>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="cart-btn" @click="addToCart">
				<text class="btn-text">加入购物车</text>
			</view>
			<view class="buy-btn" @click="buyNow">
				<text class="btn-text">立即购买</text>
			</view>
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				goodsId: null,
				goodsDetail: {},
				quantity: 1
			}
		},
		onLoad(options) {
			this.$api = api;
			if (options.id) {
				this.goodsId = parseInt(options.id);
				this.getGoodsDetail();
			} else {
				uni.showToast({
					icon: 'none',
					title: '商品ID无效'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}
		},
		methods: {
			// 获取商品详情
			async getGoodsDetail() {
				if (!this.goodsId) {
					return;
				}
				try {
					const res = await this.$api.get(`/goods/getById/${this.goodsId}`, {}, {
						loading: true,
						showError: true
					});
					if (res && res.code === 200 && res.data) {
						this.goodsDetail = res.data;
					} else {
						uni.showToast({
							icon: 'none',
							title: res?.message || '获取商品详情失败'
						});
					}
				} catch (error) {
					console.error('获取商品详情失败:', error);
					uni.showToast({
						icon: 'none',
						title: '获取商品详情失败，请稍后重试'
					});
				}
			},
			
			// 格式化价格
			formatPrice(price) {
				if (!price) return '0.00';
				return price.toFixed(2);
			},
			
			// 加入购物车
			async addToCart() {
				// 检查登录状态
				const token = uni.getStorageSync('token');
				if (!token) {
					uni.showModal({
						title: '提示',
						content: '请先登录',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({
									url: '/pages/login/login'
								});
							}
						}
					});
					return;
				}
				
				if (!this.goodsId) {
					uni.showToast({
						icon: 'none',
						title: '商品信息错误'
					});
					return;
				}
				
				uni.showLoading({
					title: '添加中...'
				});
				
				try {
					const res = await this.$api.post('/cart/add', {
						goodsId: this.goodsId,
						quantity: this.quantity
					}, {
						loading: false,
						showError: true
					});
					
					uni.hideLoading();
					
					if (res && res.code === 200) {
						uni.showToast({
							icon: 'success',
							title: '已加入购物车',
							duration: 2000
						});
					}
				} catch (error) {
					uni.hideLoading();
					console.error('加入购物车失败:', error);
				}
			},
			
			// 立即购买
			async buyNow() {
				// 检查登录状态
				const token = uni.getStorageSync('token');
				if (!token) {
					uni.showModal({
						title: '提示',
						content: '请先登录',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({
									url: '/pages/login/login'
								});
							}
						}
					});
					return;
				}
				
				// 跳转到下单页面
				uni.navigateTo({
					url: `/pages/order/create?goodsId=${this.goodsId}&quantity=${this.quantity}`
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding-bottom: 120rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}
	
	.goods-image {
		width: 100%;
		height: 750rpx;
		background-color: #fff;
		
		.image {
			width: 100%;
			height: 100%;
		}
	}
	
	.goods-info {
		background-color: #fff;
		padding: 30rpx;
		margin-top: 20rpx;
		
		.goods-name {
			font-size: 36rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 20rpx;
			line-height: 1.5;
		}
		
		.goods-price {
			display: flex;
			align-items: baseline;
			margin-bottom: 20rpx;
			
			.price-symbol {
				font-size: 28rpx;
				color: #ff4444;
				font-weight: bold;
			}
			
			.price-value {
				font-size: 48rpx;
				color: #ff4444;
				font-weight: bold;
			}
		}
		
		.goods-meta {
			display: flex;
			gap: 30rpx;
			font-size: 28rpx;
			color: #999;
			
			.meta-item {
				flex: 1;
			}
		}
	}
	
	.goods-detail {
		background-color: #fff;
		padding: 30rpx;
		margin-top: 20rpx;
		
		.detail-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 20rpx;
			padding-bottom: 20rpx;
			border-bottom: 1rpx solid #eee;
		}
		
		.detail-item {
			display: flex;
			padding: 15rpx 0;
			font-size: 28rpx;
			
			.label {
				color: #999;
				width: 120rpx;
			}
			
			.value {
				color: #333;
				flex: 1;
			}
		}
	}
	
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		height: 100rpx;
		background-color: #fff;
		border-top: 1rpx solid #eee;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
		
		.cart-btn,
		.buy-btn {
			flex: 1;
			display: flex;
			align-items: center;
			justify-content: center;
			height: 100%;
			
			.btn-text {
				font-size: 32rpx;
				font-weight: bold;
			}
		}
		
		.cart-btn {
			background-color: #ff9500;
			color: #fff;
		}
		
		.buy-btn {
			background-color: #ff4444;
			color: #fff;
		}
	}
</style>

