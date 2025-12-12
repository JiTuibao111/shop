<template>
	<view class="container">
		<!-- 订单状态筛选 -->
		<view class="status-tabs">
			<view 
				class="tab-item" 
				:class="{ active: currentStatus === null }"
				@click="changeStatus(null)"
			>
				全部
			</view>
			<view 
				class="tab-item" 
				:class="{ active: currentStatus === 0 }"
				@click="changeStatus(0)"
			>
				待支付
			</view>
			<view 
				class="tab-item" 
				:class="{ active: currentStatus === 1 }"
				@click="changeStatus(1)"
			>
				已支付
			</view>
			<view 
				class="tab-item" 
				:class="{ active: currentStatus === 3 }"
				@click="changeStatus(3)"
			>
				已完成
			</view>
		</view>
		
		<!-- 订单列表 -->
		<view class="order-list" v-if="orderList.length > 0">
			<view class="order-item" v-for="order in orderList" :key="order.orderId" @click="toDetail(order.orderId)">
				<view class="order-header">
					<text class="order-no">订单号：{{ order.orderNo }}</text>
					<text class="order-status">{{ getStatusText(order.status) }}</text>
				</view>
				
				<view class="order-goods">
					<view 
						class="goods-item" 
						v-for="item in order.orderItems" 
						:key="item.itemId"
					>
						<image :src="item.goods?.image || '/static/logo.png'" mode="aspectFill" class="goods-image"></image>
						<view class="goods-info">
							<view class="goods-name">{{ item.goods?.goodsName || '商品名称' }}</view>
							<view class="goods-price-quantity">
								<text class="price">¥{{ formatPrice(item.price) }}</text>
								<text class="quantity">x{{ item.quantity }}</text>
							</view>
						</view>
					</view>
				</view>
				
				<view class="order-footer">
					<text class="total-amount">合计：¥{{ formatPrice(order.totalAmount) }}</text>
					<view class="action-btns">
						<view 
							class="action-btn cancel-btn" 
							v-if="order.status === 0 || order.status === 1"
							@click.stop="cancelOrder(order.orderId)"
						>
							取消订单
						</view>
						<view 
							class="action-btn pay-btn" 
							v-if="order.status === 0"
							@click.stop="payOrder(order.orderId)"
						>
							去支付
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="empty-order" v-else>
			<text class="empty-text">暂无订单</text>
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				currentStatus: null,
				orderList: []
			}
		},
		onLoad() {
			this.$api = api;
		},
		onShow() {
			this.getOrderList();
		},
		methods: {
			// 获取订单列表
			async getOrderList() {
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
					const params = {};
					if (this.currentStatus !== null) {
						params.status = this.currentStatus;
					}
					const res = await this.$api.get('/order/list', params);
					if (res && res.data) {
						this.orderList = res.data;
					}
				} catch (error) {
					console.error('获取订单列表失败:', error);
				}
			},
			
			// 切换订单状态
			changeStatus(status) {
				this.currentStatus = status;
				this.getOrderList();
			},
			
			// 获取状态文本
			getStatusText(status) {
				const statusMap = {
					0: '待支付',
					1: '已支付',
					2: '已发货',
					3: '已完成',
					4: '已取消'
				};
				return statusMap[status] || '未知';
			},
			
			// 跳转到订单详情
			toDetail(orderId) {
				uni.navigateTo({
					url: `/pages/order/detail?id=${orderId}`
				});
			},
			
			// 取消订单
			async cancelOrder(orderId) {
				uni.showModal({
					title: '提示',
					content: '确定要取消这个订单吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const result = await this.$api.put(`/order/cancel/${orderId}`);
								if (result && result.code === 200) {
									uni.showToast({
										icon: 'success',
										title: '订单已取消'
									});
									this.getOrderList();
								}
							} catch (error) {
								console.error('取消订单失败:', error);
							}
						}
					}
				});
			},
			
			// 支付订单
			payOrder(orderId) {
				uni.showToast({
					icon: 'none',
					title: '支付功能待开发'
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
		background-color: #f5f5f5;
		min-height: 100vh;
	}
	
	.status-tabs {
		display: flex;
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
		
		.tab-item {
			flex: 1;
			text-align: center;
			padding: 15rpx 0;
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
	
	.order-list {
		padding: 0 20rpx;
		
		.order-item {
			background-color: #fff;
			border-radius: 20rpx;
			margin-bottom: 20rpx;
			padding: 30rpx;
			
			.order-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding-bottom: 20rpx;
				border-bottom: 1rpx solid #eee;
				margin-bottom: 20rpx;
				
				.order-no {
					font-size: 28rpx;
					color: #666;
				}
				
				.order-status {
					font-size: 28rpx;
					color: #ff4444;
					font-weight: bold;
				}
			}
			
			.order-goods {
				margin-bottom: 20rpx;
				
				.goods-item {
					display: flex;
					margin-bottom: 20rpx;
					
					&:last-child {
						margin-bottom: 0;
					}
					
					.goods-image {
						width: 150rpx;
						height: 150rpx;
						border-radius: 10rpx;
						margin-right: 20rpx;
					}
					
					.goods-info {
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: space-between;
						
						.goods-name {
							font-size: 28rpx;
							color: #333;
							margin-bottom: 10rpx;
						}
						
						.goods-price-quantity {
							display: flex;
							justify-content: space-between;
							
							.price {
								font-size: 30rpx;
								color: #ff4444;
								font-weight: bold;
							}
							
							.quantity {
								font-size: 28rpx;
								color: #999;
							}
						}
					}
				}
			}
			
			.order-footer {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding-top: 20rpx;
				border-top: 1rpx solid #eee;
				
				.total-amount {
					font-size: 32rpx;
					color: #ff4444;
					font-weight: bold;
				}
				
				.action-btns {
					display: flex;
					gap: 20rpx;
					
					.action-btn {
						padding: 10rpx 30rpx;
						border-radius: 10rpx;
						font-size: 26rpx;
					}
					
					.cancel-btn {
						background-color: #f5f5f5;
						color: #666;
					}
					
					.pay-btn {
						background-color: #ff4444;
						color: #fff;
					}
				}
			}
		}
	}
	
	.empty-order {
		display: flex;
		justify-content: center;
		align-items: center;
		padding-top: 300rpx;
		
		.empty-text {
			font-size: 32rpx;
			color: #999;
		}
	}
</style>

