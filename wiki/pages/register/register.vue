<template>
	<view class="content">
		<view class="loginBox">
			<!-- <h3>登录</h3> -->
			<h3 style="text-align: center;margin-bottom:60rpx;">欢迎注册</h3>
			<view class="inputBox">
				<view class="ipt">
					<uni-icons type="contact" size="24" color="rgb(247,120,172)"></uni-icons>
					<input type="text" value="" v-model="username" placeholder="请输入账号" />
				</view>
				<view class="ipt">
					<uni-icons type="eye" size="24" color="rgb(247,120,172)">
					</uni-icons>
					<input type="password" value="" v-model="password" placeholder="请输入密码" />
				</view>
				<view class="ipt">
					<uni-icons type="eye" size="24" color="rgb(247,120,172)">
					</uni-icons>
					<input type="password" value="" v-model="repeatPassword" placeholder="请输入确认密码" />
				</view>
				<!-- <view class="ipt">
<uni-icons type="checkmarkempty" size="24"
color="rgb(247,120,172)"></uni-icons>
<input type="text" value="" placeholder="请输入验证码" />
<view class="yzm">
验证码
</view>
</view> -->
				<view class="forgetPwd">
					<span>忘记密码</span>
					<span @click="toLogin">有账号，去登录</span>
				</view>
				<button @click="register">注册</button>
			</view>
			<view class="tipbox">
				<view class="txt">
					—— 其他账号登录 ——
				</view>
				<view class="otherUser">
					<uni-icons type="qq" size="40" color="rgb(66,157,250)">
					</uni-icons>
					<uni-icons type="weixin" size="40" color="rgb(2,187,17)">
					</uni-icons>
				</view>
			</view>
		</view>
		<view class="tip">
			某某应用 2024
		</view>
	</view>
</template>
<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				username: "",
				password: "",
				repeatPassword: "",
				loading: false
			}
		},
		onLoad() {
			// 将api挂载到this上
			this.$api = api;
		},
		methods: {
			register() {
				// 防止重复提交
				if (this.loading) return;
				
				// 参数验证
				if (!this.username.trim()) {
					uni.showToast({
						icon: "none",
						title: "请输入用户名"
					});
					return;
				}
				
				if (!this.password.trim()) {
					uni.showToast({
						icon: "none",
						title: "请输入密码"
					});
					return;
				}
				
				if (this.password.length < 6) {
					uni.showToast({
						icon: "none",
						title: "密码长度至少6位"
					});
					return;
				}
				
				if (this.password !== this.repeatPassword) {
					uni.showToast({
						icon: "none",
						title: "两次输入的密码不一致"
					});
					return;
				}
				
				this.loading = true;
				
				this.$api.post('/WxUser/register', {
					username: this.username.trim(),
					password: this.password
				}, {
					loading: true,
					loadingText: '注册中...',
					showError: true
				}).then(res => {
					this.loading = false;
					
					if (res && res.code === 200) {
						uni.showToast({
							icon: "success",
							title: res.message || "注册成功",
							duration: 2000
						});
						
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/login/login'
							});
						}, 2000);
					}
				}).catch(err => {
					this.loading = false;
					console.error('注册失败:', err);
				});
			},
			toLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			}
		}
	}
</script>

<style scoped>
	.content {
		height: 100vh;
		background: url("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F10%2F20200510005139_JR8fL.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1714820289&t=e835cde99a094cbd98f9c318f25160ec") no-repeat;
		background-size: 100% 100%;
	}

	.loginBox {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -60%);
		width: 90%;
		background-color: #fff;
		border-radius: 20rpx;
		padding: 60rpx;
		box-sizing: border-box;
	}

	h3 {
		color: rgb(247, 120, 172);
		font-size: 40rpx;
		letter-spacing: 10rpx;
		margin-bottom: 40rpx;
	}

	.inputBox {}

	.ipt {
		height: 86rpx;
		display: flex;
		justify-content: flex-start;
		align-items: center;
		margin-bottom: 20rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
		padding-left: 10rpx;
	}

	.ipt input {
		margin-left: 20rpx;
		font-size: 28rpx;
	}

	.ipt input {
		margin-left: 20rpx;
	}

	.forgetPwd {
		font-size: 26rpx;
		color: #b5b5b5;
		text-align: end;
		padding: 0 10rpx;
		display: flex;
		justify-content: space-between;
	}

	button {
		margin-top: 20rpx;
		line-height: 85rpx;
		text-align: center;
		background: linear-gradient(to right, rgb(255, 170, 127), rgb(247, 120, 172));
		border-radius: 40rpx;
		color: #fff;
		margin-top: 40rpx;
	}

	.tip {
		text-align: center;
		font-size: 28rpx;
		position: fixed;
		bottom: 50rpx;
		left: 50%;
		transform: translate(-50%, -50%);
		color: #f4f4f4;
	}

	.tipbox {
		text-align: center;
		margin-top: 100rpx;
	}

	.otherUser {
		margin-top: 30rpx;
		display: flex;
		justify-content: center;
	}

	.txt {
		font-size: 28rpx;
		color: #cbcbcb;
	}

	.otherUser .uni-icons {
		margin-left: 20rpx;
	}

	.yzm {
		text-align: end;
		font-size: 24rpx;
		background: linear-gradient(to right, rgb(66, 157, 250), rgb(0, 170, 127));
		height: 60rpx;
		width: 150rpx;
		line-height: 60rpx;
		text-align: center;
		border-radius: 10rpx;
		color: #fff;
	}
</style>