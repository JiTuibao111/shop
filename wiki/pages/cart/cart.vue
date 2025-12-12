<template>
	<view class="container">
		<view class="cart-list" v-if="cartList.length > 0">
			<view class="cart-item" v-for="item in cartList" :key="item.cartId">
				<view class="item-content" @click="toDetail(item.goodsId)">
					<image :src="item.goods?.image || '/static/logo.png'" mode="aspectFill" class="goods-image"></image>
					<view class="goods-info">
						<view class="goods-name">{{ item.goods?.goodsName || '商品名称' }}</view>
						<view class="goods-price">
							<text class="price-symbol">¥</text>
							<text class="price-value">{{ formatPrice(item.goods?.price) }}</text>
						</view>
						<view class="stock-info">库存：{{ item.goods?.stock || 0 }}</view>
					</view>
				</view>
				<view class="item-actions">
					<uni-number-box 
						:value="item.quantity" 
						:min="1" 
						:max="item.goods?.stock || 1"
						@change="updateQuantity(item.cartId, $event)"
					></uni-number-box>
					<view class="delete-btn" @click="removeItem(item.cartId)">
						<text>删除</text>
					</view>
				</view>
			</view>
		</view>
		
		<view class="empty-cart" v-else>
			<text class="empty-text">购物车是空的</text>
			<view class="go-shopping" @click="goShopping">去逛逛</view>
		</view>
		
		<!-- 底部结算栏 -->
		<view class="bottom-bar" v-if="cartList.length > 0">
			<view class="total-info">
				<text class="total-text">共{{ totalQuantity }}件商品</text>
				<text class="total-price">
					合计：<text class="price-symbol">¥</text>{{ formatPrice(totalAmount) }}
				</text>
			</view>
			<view class="checkout-btn" @click="checkout">
				<text>去结算</text>
			</view>
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				cartList: []
			}
		},
		onLoad() {
			this.$api = api;
		},
		onShow() {
			this.getCartList();
		},
		computed: {
			totalQuantity() {
				return this.cartList.reduce((sum, item) => sum + item.quantity, 0);
			},
			totalAmount() {
				return this.cartList.reduce((sum, item) => {
					const price = item.goods?.price || 0;
					return sum + (price * item.quantity);
				}, 0);
			}
		},
		methods: {
			// 获取购物车列表
			async getCartList() {
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
				
				try {
					const res = await this.$api.get('/cart/list');
					if (res && res.data) {
						this.cartList = res.data;
					}
				} catch (error) {
					console.error('获取购物车失败:', error);
				}
			},
			
			// 更新数量
			async updateQuantity(cartId, quantity) {
				try {
					const res = await this.$api.put('/cart/update', {
						cartId: cartId,
						quantity: quantity
					});
					
					if (res && res.code === 200) {
						// 更新本地数据
						const item = this.cartList.find(c => c.cartId === cartId);
						if (item) {
							item.quantity = quantity;
						}
					}
				} catch (error) {
					console.error('更新数量失败:', error);
				}
			},
			
			// 删除商品
			async removeItem(cartId) {
				uni.showModal({
					title: '提示',
					content: '确定要删除这个商品吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const result = await this.$api.delete(`/cart/remove/${cartId}`);
								if (result && result.code === 200) {
									uni.showToast({
										icon: 'success',
										title: '删除成功'
									});
									this.getCartList();
								}
							} catch (error) {
								console.error('删除失败:', error);
							}
						}
					}
				});
			},
			
			// 去结算
			checkout() {
				if (this.cartList.length === 0) {
					uni.showToast({
						icon: 'none',
						title: '购物车是空的'
					});
					return;
				}
				
				const cartIds = this.cartList.map(item => item.cartId);
				uni.navigateTo({
					url: `/pages/order/create?cartIds=${cartIds.join(',')}`
				});
			},
			
			// 跳转到商品详情
			toDetail(goodsId) {
				uni.navigateTo({
					url: `/pages/detail/detail?id=${goodsId}`
				});
			},
			
			// 去逛逛
			goShopping() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			
			// 格式化价格
			formatPrice(price) {
				if (!price) return '0.00';
				return price.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		min-height: 100vh;
		background-color: #f5f5f5;
		padding-bottom: 120rpx;
	}
	
	.cart-list {
		padding: 20rpx;
		
		.cart-item {
			background-color: #fff;
			border-radius: 20rpx;
			margin-bottom: 20rpx;
			padding: 20rpx;
			display: flex;
			flex-direction: column;
			
			.item-content {
				display: flex;
				margin-bottom: 20rpx;
				
				.goods-image {
					width: 200rpx;
					height: 200rpx;
					border-radius: 10rpx;
					margin-right: 20rpx;
				}
				
				.goods-info {
					flex: 1;
					display: flex;
					flex-direction: column;
					justify-content: space-between;
					
					.goods-name {
						font-size: 30rpx;
						color: #333;
						margin-bottom: 10rpx;
						display: -webkit-box;
						-webkit-line-clamp: 2;
						-webkit-box-orient: vertical;
						overflow: hidden;
					}
					
					.goods-price {
						display: flex;
						align-items: baseline;
						
						.price-symbol {
							font-size: 24rpx;
							color: #ff4444;
						}
						
						.price-value {
							font-size: 36rpx;
							color: #ff4444;
							font-weight: bold;
						}
					}
					
					.stock-info {
						font-size: 24rpx;
						color: #999;
						margin-top: 10rpx;
					}
				}
			}
			
			.item-actions {
				display: flex;
				justify-content: space-between;
				align-items: center;
				
				.delete-btn {
					padding: 10rpx 30rpx;
					background-color: #ff4444;
					color: #fff;
					border-radius: 10rpx;
					font-size: 28rpx;
				}
			}
		}
	}
	
	.empty-cart {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 300rpx;
		
		.empty-text {
			font-size: 32rpx;
			color: #999;
			margin-bottom: 40rpx;
		}
		
		.go-shopping {
			padding: 20rpx 60rpx;
			background-color: #1296db;
			color: #fff;
			border-radius: 50rpx;
			font-size: 30rpx;
		}
	}
	
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: center;
		height: 100rpx;
		background-color: #fff;
		border-top: 1rpx solid #eee;
		padding: 0 20rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
		
		.total-info {
			flex: 1;
			display: flex;
			flex-direction: column;
			
			.total-text {
				font-size: 24rpx;
				color: #999;
			}
			
			.total-price {
				font-size: 32rpx;
				color: #ff4444;
				font-weight: bold;
				
				.price-symbol {
					font-size: 24rpx;
				}
			}
		}
		
		.checkout-btn {
			padding: 20rpx 60rpx;
			background-color: #ff4444;
			color: #fff;
			border-radius: 50rpx;
			font-size: 30rpx;
			font-weight: bold;
		}
	}
</style>

